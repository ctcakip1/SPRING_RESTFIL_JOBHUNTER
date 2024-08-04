package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber s) throws IdInvalidException {
        if (this.subscriberService.isEmailExist(s.getEmail())) {
            throw new IdInvalidException("email da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(s));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber s) throws IdInvalidException {
        Subscriber currentSubscriber = this.subscriberService.fetchById(s.getId());
        if (currentSubscriber == null) {
            throw new IdInvalidException("Id " + s.getId() + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.subscriberService.update(s));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
