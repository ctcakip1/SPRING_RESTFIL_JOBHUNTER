package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updatedAt;
    private CompanyUser company;
    private RoleUser role;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
    }

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
    }

}
