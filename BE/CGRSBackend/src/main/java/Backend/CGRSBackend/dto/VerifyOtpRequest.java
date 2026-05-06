package Backend.CGRSBackend.dto;

import lombok.Data;

/**
 * Request body for POST /auth/verify-otp.
 * Both fields are required and validated in OtpService.
 */
@Data
public class VerifyOtpRequest {

    /** 12-digit Aadhaar number used when the OTP was requested. */
    private String aadhaarNumber;

    /** 6-digit OTP that was printed to console during send-otp. */
    private String otp;
}
