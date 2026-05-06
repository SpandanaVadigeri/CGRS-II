package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for POST /auth/verify-otp.
 * On success, returns the JWT token and user details.
 * Mirrors LoginResponse so the mobile app can handle both flows identically.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerifyResponse {

    /** JWT Bearer token — valid for 24 hours. */
    private String token;

    /** Email address linked to this Aadhaar account. */
    private String email;

    /** Role of the user: CITIZEN | AUTHORITY | ADMIN. */
    private String role;

    /** Human-readable message, e.g. "OTP verified successfully". */
    private String message;
}
