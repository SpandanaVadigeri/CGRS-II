package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Feedback;
import Backend.CGRSBackend.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByGrievance(Grievance grievance);
}
