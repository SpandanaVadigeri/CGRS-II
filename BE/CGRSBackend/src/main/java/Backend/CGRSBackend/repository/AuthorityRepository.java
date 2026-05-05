package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Authority;
import Backend.CGRSBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByUser(User user);
    Optional<Authority> findByUserId(Long userId);
}
