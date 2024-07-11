package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("send simple email")
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("lhta43251@gmail.com", "Testing send email",
        // "<h1> <b> hello </b> </h1>", false,
        // true);
        this.emailService.sendEmailFromTemplateSync("lhta43251@gmail.com", "Testing send email", "job");
        return "ok";
    }

}
