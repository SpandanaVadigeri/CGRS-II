package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response returned after successful registration or login.
 * Contains the JWT token and basic user information.
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String role;
}
