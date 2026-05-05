package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Grievance;
import Backend.CGRSBackend.entity.StatusUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatusUpdateRepository extends JpaRepository<StatusUpdate, Long> {
    List<StatusUpdate> findByGrievanceOrderByUpdatedAtDesc(Grievance grievance);
}
