package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Citizen;
import Backend.CGRSBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByUser(User user);
    Optional<Citizen> findByUserId(Long userId);
}
