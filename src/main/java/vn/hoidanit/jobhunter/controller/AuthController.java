package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // set thong tin nguoi dung dang nhap vao context (co the su dung sau này)
                SecurityContextHolder.getContext().setAuthentication(authentication);
                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        res.setUser(userLogin);
                }
                // create access token
                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
                res.setAccess_token(access_token);
                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
                // update user
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                // set cookies
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account message")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User currentUserDB = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.UserGetAccount UserGetAccount = new ResLoginDTO.UserGetAccount();
                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getEmail());
                        userLogin.setName(currentUserDB.getName());
                        UserGetAccount.setUser(userLogin);
                }
                return ResponseEntity.ok().body(UserGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get user by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("abc")) {
                        throw new IdInvalidException("Ban khong co refresh token o cookie");
                }

                // check valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Refresh Token khong hop le");
                }
                // issue new token/set refresh token as cookies
                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(email);
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        res.setUser(userLogin);
                }
                // create access token
                String access_token = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccess_token(access_token);
                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
                // update user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookies
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);

        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout User")
        public ResponseEntity<Void> postLogOut(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {

                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                if (email.equals("")) {
                        throw new IdInvalidException("Access Token khong hop le");
                }
                // update refresh token = null
                this.userService.updateUserToken(null, email);
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED)
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }

}
