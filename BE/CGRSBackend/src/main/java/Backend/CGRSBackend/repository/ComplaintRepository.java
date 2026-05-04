package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Complaint entity.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Fetch all complaints belonging to a specific user
    List<Complaint> findByUserId(Long userId);
}
