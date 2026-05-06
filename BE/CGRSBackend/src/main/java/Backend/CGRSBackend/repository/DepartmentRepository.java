package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Department entity.
 * Phase 3: added duplicate-check and name-lookup methods.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
}
