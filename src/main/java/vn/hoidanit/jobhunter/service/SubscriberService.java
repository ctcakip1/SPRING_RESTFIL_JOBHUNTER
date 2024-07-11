package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public Subscriber create(Subscriber s) {
        // check skills
        if (s.getSkills() != null) {
            List<Long> reqSkills = s.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            s.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(s);
    }

    public boolean isEmailExist(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber fetchById(long id) {
        return this.subscriberRepository.findById(id);
    }

    public Subscriber update(Subscriber s) {
        Subscriber subscriber = this.subscriberRepository.findById(s.getId());
        if (s.getSkills() != null) {
            List<Long> reqSkills = s.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            s.setSkills(dbSkills);
        }
        if (subscriber != null) {
            subscriber.setSkills(s.getSkills());
            this.subscriberRepository.save(subscriber);
            return subscriber;
        }
        return null;
    }
}
