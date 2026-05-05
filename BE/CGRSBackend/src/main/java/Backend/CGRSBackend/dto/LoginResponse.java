package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Returned after successful login — contains the JWT token.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}
