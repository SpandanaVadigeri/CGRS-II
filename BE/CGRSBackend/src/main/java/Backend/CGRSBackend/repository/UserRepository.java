package Backend.CGRSBackend.repository;

import Backend.CGRSBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity — provides CRUD + custom lookups.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login and JWT authentication filter
    Optional<User> findByEmail(String email);

    // Used during registration to check for duplicates
    boolean existsByEmail(String email);

    // Aadhaar OTP login — look up user by Aadhaar number
    Optional<User> findByAadhaarNumber(String aadhaarNumber);

    // Prevent duplicate Aadhaar during registration
    boolean existsByAadhaarNumber(String aadhaarNumber);
}

