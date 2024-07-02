package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(long id) {
        return this.skillRepository.findById(id);
    }

    public Skill handleUpdateSkill(Skill skill) {
        Skill currentSkill = this.skillRepository.findById(skill.getId());
        if (currentSkill != null) {
            currentSkill.setName(skill.getName());
            this.skillRepository.save(currentSkill);
        }

        return currentSkill;
    }

    public void handleDeleteSkill(long id) {
        // delete job (inside job_skill table)
        Skill skill = this.skillRepository.findById(id);
        skill.getJobs().forEach(job -> job.getSkills().remove(skill));
        // delete skill
        this.skillRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

}
