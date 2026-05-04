package Backend.CGRSBackend.dto;

import lombok.Data;

/**
 * Incoming request body for user login.
 */
@Data
public class LoginRequest {

    private String email;
    private String password;
}
