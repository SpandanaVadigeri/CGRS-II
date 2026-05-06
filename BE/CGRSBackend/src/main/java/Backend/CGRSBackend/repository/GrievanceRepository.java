package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 3: added department-based filtering and escalation threshold query.
 */
@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findByCitizen(Citizen citizen);
    List<Grievance> findByAuthority(Authority authority);
    List<Grievance> findByStatus(GrievanceStatus status);
    List<Grievance> findByCitizenOrderByCreatedAtDesc(Citizen citizen);

    /** Phase 3 — Department Routing: get grievances by department */
    List<Grievance> findByDepartmentOrderByCreatedAtDesc(Department department);

    /**
     * Phase 3 — Escalation: find grievances that are still open
     * (PENDING or IN_PROGRESS) and were created before a cutoff time.
     */
    @Query("SELECT g FROM Grievance g WHERE g.status IN :statuses AND g.createdAt < :cutoff")
    List<Grievance> findOpenGrievancesOlderThan(
            @Param("statuses") List<GrievanceStatus> statuses,
            @Param("cutoff") LocalDateTime cutoff
    );
}
