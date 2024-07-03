package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.User;

@Getter
@Setter
public class ResCreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
