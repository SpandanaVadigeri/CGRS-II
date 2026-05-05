package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Attachment;
import Backend.CGRSBackend.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByGrievance(Grievance grievance);
}
