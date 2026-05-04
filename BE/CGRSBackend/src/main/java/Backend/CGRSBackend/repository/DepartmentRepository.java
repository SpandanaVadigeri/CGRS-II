package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Department entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
