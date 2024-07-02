package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;

    }

    public Job fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageSkill = this.jobRepository.findAll(spec, pageable);
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

    public ResCreateJobDTO handleCreateAJob(Job job) {
        // check skill (khong lay nhung skill co id khong hop le khi truyen len)
        if (job.getSkills() != null) {
            List<Long> reqKills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqKills);
            job.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setLevel(currentJob.getLevel());
        dto.setActive(currentJob.isActive());
        dto.setLocation(currentJob.getLocation());
        dto.setName(currentJob.getName());
        dto.setQuantity(currentJob.getQuantity());
        dto.setSalary(currentJob.getSalary());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public ResUpdateJobDTO handleUpdateAJob(Job job) {
        // check skill (khong lay nhung skill co id khong hop le khi truyen len)
        if (job.getSkills() != null) {
            List<Long> reqKills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqKills);
            job.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setLevel(currentJob.getLevel());
        dto.setActive(currentJob.isActive());
        dto.setLocation(currentJob.getLocation());
        dto.setName(currentJob.getName());
        dto.setQuantity(currentJob.getQuantity());
        dto.setSalary(currentJob.getSalary());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }
}
