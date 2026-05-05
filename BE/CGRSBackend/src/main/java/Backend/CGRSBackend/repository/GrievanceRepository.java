package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findByCitizen(Citizen citizen);
    List<Grievance> findByAuthority(Authority authority);
    List<Grievance> findByStatus(GrievanceStatus status);
    List<Grievance> findByCitizenOrderByCreatedAtDesc(Citizen citizen);
}
