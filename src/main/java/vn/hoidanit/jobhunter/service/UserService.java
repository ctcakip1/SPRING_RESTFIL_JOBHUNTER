package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;

import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service

public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;

    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Company company = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(company != null ? company : null);
        }
        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.findRoleById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }
        return this.userRepository.save(user);
    }

    public void hanleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserById(long id) {
        User user = this.userRepository.findById(id);
        if (user != null) {
            return user;
        }
        return null;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser r = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            res.setCompany(com);
        }
        if (user.getRole() != null) {
            r.setId(user.getRole().getId());
            res.setRole(r);
        }
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        ResCreateUserDTO.RoleUser r = new ResCreateUserDTO.RoleUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            res.setCompany(com);
        }

        if (user.getRole() != null) {
            r.setId(user.getRole().getId());
            res.setRole(r);
        }
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        ResUpdateUserDTO.RoleUser r = new ResUpdateUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            res.setCompany(com);
        }
        if (user.getRole() != null) {
            r.setId(user.getRole().getId());
            res.setRole(r);
        }
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public ResultPaginationDTO handleGetAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.userRepository.findById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setGender(user.getGender());
            currentUser.setAge(user.getAge());

            // check company
            if (user.getCompany() != null) {
                Company company = this.companyService.getCompanyById(user.getCompany().getId());
                currentUser.setCompany(company != null ? company : null);
            }
            // check role
            if (user.getRole() != null) {
                Role r = this.roleService.findRoleById(user.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
