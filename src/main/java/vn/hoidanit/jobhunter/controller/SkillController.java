package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createASkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName() + " da ton tai");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("update a skill")
    public ResponseEntity<Skill> updateASkill(@Valid @RequestBody Skill postManSkill) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(postManSkill.getId());
        // check id
        if (currentSkill == null) {
            throw new IdInvalidException("Skill voi id = " + postManSkill.getId() + " khong ton tai");
        }
        // check name
        if (postManSkill.getName() != null && this.skillService.isNameExist(postManSkill.getName())) {
            throw new IdInvalidException(
                    "SKill voi name = " + postManSkill.getName() + " da ton tai, vui long su dung ten khac");
        }
        Skill skill = this.skillService.handleUpdateSkill(postManSkill);
        return ResponseEntity.ok().body(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec,
            Pageable pageable) {
        ResultPaginationDTO skills = this.skillService.fetchAllSkill(spec, pageable);
        return ResponseEntity.ok().body(skills);
    }

    @DeleteMapping("/skills")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteASkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        // check id
        if (currentSkill == null) {
            throw new IdInvalidException("Skill voi id = " + id + " khong ton tai");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
