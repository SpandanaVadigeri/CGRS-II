package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.Role;
import lombok.Data;

/**
 * Incoming request body for user registration.
 */
@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role; // Optional: defaults to CITIZEN if not provided
}
