package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Returned after successful registration — NO JWT token here.
 */
@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String name;
    private String email;
    private String role;
}
