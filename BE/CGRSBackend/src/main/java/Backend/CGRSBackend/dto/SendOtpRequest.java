package Backend.CGRSBackend.dto;

import lombok.Data;

/**
 * Request body for POST /auth/send-otp.
 * aadhaarNumber must be exactly 12 digits (validated in OtpService).
 */
@Data
public class SendOtpRequest {

    /** 12-digit Aadhaar identification number. */
    private String aadhaarNumber;
}
