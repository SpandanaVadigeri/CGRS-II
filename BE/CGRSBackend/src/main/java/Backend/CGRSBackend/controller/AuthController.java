package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.*;
import Backend.CGRSBackend.service.OtpService;
import Backend.CGRSBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public authentication endpoints — no JWT required.
 *
 * Existing endpoints (unchanged):
 *   POST /auth/register  — email + password registration
 *   POST /auth/login     — email + password → JWT
 *
 * Phase 3 additions — Aadhaar OTP flow:
 *   POST /auth/send-otp   — generate & "send" OTP for a given Aadhaar
 *   POST /auth/verify-otp — verify OTP → JWT token (same token format as /login)
 *
 * Both login paths produce the same JWT — downstream APIs are unaffected.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    // ── Existing endpoints ────────────────────────────────────────────────────

    /**
     * POST /auth/register
     * Registers a new user. Returns user info — NO JWT token.
     * Now accepts optional aadhaarNumber to enable OTP login.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * POST /auth/login
     * Authenticates user with email + password → returns JWT token.
     * This endpoint is unchanged — existing clients continue to work.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ── Aadhaar OTP endpoints (new) ───────────────────────────────────────────

    /**
     * POST /auth/send-otp
     * Step 1 of Aadhaar OTP login.
     *
     * Generates a 6-digit OTP for the given Aadhaar number and prints it
     * to the server console (simulation — replace with SMS gateway in production).
     *
     * Validations:
     *   - aadhaarNumber must be exactly 12 digits
     *   - A registered user with this Aadhaar must exist
     *
     * Request body:
     * {
     *   "aadhaarNumber": "123456789012"
     * }
     *
     * Response: 200 OK with message string
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody SendOtpRequest request) {
        String message = otpService.generateOtp(request.getAadhaarNumber());
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * POST /auth/verify-otp
     * Step 2 of Aadhaar OTP login.
     *
     * Verifies the OTP. On success, returns a JWT token identical in format
     * to the one issued by /auth/login — the mobile app handles both the same way.
     *
     * Validations:
     *   - aadhaarNumber must be exactly 12 digits
     *   - otp must be exactly 6 digits
     *   - OTP must not be expired (5-minute window)
     *   - OTP must match the stored value
     *
     * Request body:
     * {
     *   "aadhaarNumber": "123456789012",
     *   "otp": "482910"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGci...",
     *   "email": "citizen@example.com",
     *   "role": "CITIZEN",
     *   "message": "OTP verified successfully"
     * }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerifyResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        OtpVerifyResponse response = otpService.verifyOtp(
                request.getAadhaarNumber(),
                request.getOtp()
        );
        return ResponseEntity.ok(response);
    }
}
