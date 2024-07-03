package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.StorageException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume r)
            throws StorageException, IdInvalidException {
        // check id exist
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(r);
        if (!isIdExist) {
            throw new IdInvalidException("User id/ Job id khong ton tai");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createAResume(r));
    }

    @PutMapping("/resumes")
    @ApiMessage("update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume r) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(r.getId());
        if (resume == null) {
            throw new IdInvalidException("Resume voi id = " + r.getId() + " khong hop le");
        }
        resume.setStatus(r.getStatus());
        return ResponseEntity.ok().body(this.resumeService.updateAResume(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);

        if (resume == null) {
            throw new IdInvalidException("Resume voi id = " + id + " khong ton tai");
        }
        this.resumeService.deleteAResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume voi id = " + id + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resumes with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Resume> spec,
            Pageable pageable) {
        ResultPaginationDTO resumes = this.resumeService.fetchAllResume(spec, pageable);
        return ResponseEntity.ok().body(resumes);
    }
}
