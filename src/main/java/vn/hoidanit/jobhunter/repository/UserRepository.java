package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hoidanit.jobhunter.domain.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findById(long id);

    User findByEmail(String username);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);

    
}
