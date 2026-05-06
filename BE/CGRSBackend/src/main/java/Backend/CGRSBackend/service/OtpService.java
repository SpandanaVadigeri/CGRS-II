package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.OtpVerifyResponse;
import Backend.CGRSBackend.entity.User;
import Backend.CGRSBackend.repository.UserRepository;
import Backend.CGRSBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aadhaar OTP Authentication Service.
 *
 * Design decisions:
 *  - OTPs are stored in a ConcurrentHashMap (in-memory) — no DB table needed.
 *    This keeps the implementation simple and avoids schema changes.
 *    Trade-off: OTPs are lost on restart (acceptable for a simulated flow).
 *  - Each map entry is a small inner record: {otp, expiryTime}.
 *  - An OTP is removed from the map immediately after successful verification
 *    (one-time use guarantee).
 *  - Aadhaar validation: exactly 12 digits.
 *  - OTP validation: exactly 6 digits, not expired, must match stored value.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /** OTP validity window in minutes. */
    private static final int OTP_EXPIRY_MINUTES = 5;

    /** In-memory OTP store: aadhaarNumber → OtpEntry */
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // ── Inner model ────────────────────────────────────────────────────────────

    /**
     * Lightweight holder for a single pending OTP.
     * No Lombok needed — it's a simple private record.
     */
    private record OtpEntry(String otp, LocalDateTime expiryTime) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Generate and "send" an OTP for the given Aadhaar number.
     *
     * Validations:
     *  1. Aadhaar must be exactly 12 digits.
     *  2. A user with this Aadhaar must already be registered.
     *
     * On success: stores OTP in memory and prints it to the console (simulation).
     *
     * @param aadhaarNumber 12-digit Aadhaar string
     * @return confirmation message
     */
    public String generateOtp(String aadhaarNumber) {
        validateAadhaar(aadhaarNumber);

        // Ensure the Aadhaar belongs to a registered user
        userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new RuntimeException(
                        "No user registered with Aadhaar: " + aadhaarNumber +
                        ". Please register first and provide your Aadhaar number."));

        // Generate 6-digit OTP (left-padded so e.g. 000123 is valid)
        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        // Store with expiry
        otpStore.put(aadhaarNumber, new OtpEntry(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));

        // Simulate OTP delivery — print to console
        log.info("╔══════════════════════════════════════╗");
        log.info("║  [OTP SIMULATION]                    ║");
        log.info("║  Aadhaar : {}         ║", aadhaarNumber);
        log.info("║  OTP     : {}                        ║", otp);
        log.info("║  Expires : {} minutes                ║", OTP_EXPIRY_MINUTES);
        log.info("╚══════════════════════════════════════╝");

        return "OTP sent successfully to the mobile number linked with Aadhaar " +
               aadhaarNumber + ". Valid for " + OTP_EXPIRY_MINUTES + " minutes.";
    }

    /**
     * Verify the OTP and — if valid — return a JWT token.
     *
     * Validations:
     *  1. Aadhaar must be exactly 12 digits.
     *  2. OTP must be exactly 6 digits.
     *  3. An OTP must exist for this Aadhaar (send-otp was called first).
     *  4. OTP must not be expired.
     *  5. OTP must match the stored value.
     *
     * On success: OTP is removed from store (one-time use), JWT is issued.
     *
     * @param aadhaarNumber 12-digit Aadhaar string
     * @param otp           6-digit OTP submitted by the user
     * @return OtpVerifyResponse with JWT token, email, role, and message
     */
    public OtpVerifyResponse verifyOtp(String aadhaarNumber, String otp) {
        validateAadhaar(aadhaarNumber);
        validateOtpFormat(otp);

        // Retrieve pending OTP
        OtpEntry entry = otpStore.get(aadhaarNumber);
        if (entry == null) {
            throw new RuntimeException(
                    "No OTP found for Aadhaar " + aadhaarNumber +
                    ". Please call /auth/send-otp first.");
        }

        // Check expiry
        if (entry.isExpired()) {
            otpStore.remove(aadhaarNumber); // clean up expired entry
            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP via /auth/send-otp.");
        }

        // Match OTP
        if (!entry.otp().equals(otp)) {
            throw new RuntimeException("Invalid OTP. Please check and try again.");
        }

        // ── OTP valid — one-time use: remove immediately ────────────────────
        otpStore.remove(aadhaarNumber);

        // Resolve the linked user
        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new RuntimeException(
                        "User not found for Aadhaar: " + aadhaarNumber));

        // Generate JWT using the user's email (same subject as password login)
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("[OTP LOGIN] Successful Aadhaar login for user: {}", user.getEmail());

        return OtpVerifyResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("OTP verified successfully")
                .build();
    }

    // ── Validation helpers ─────────────────────────────────────────────────────

    /**
     * Aadhaar number must be exactly 12 numeric digits.
     * Real Aadhaar also has a Verhoeff checksum, but we skip that for simulation.
     */
    private void validateAadhaar(String aadhaarNumber) {
        if (aadhaarNumber == null || !aadhaarNumber.matches("\\d{12}")) {
            throw new RuntimeException(
                    "Invalid Aadhaar number. Must be exactly 12 digits (numeric only).");
        }
    }

    /**
     * OTP must be exactly 6 numeric digits.
     */
    private void validateOtpFormat(String otp) {
        if (otp == null || !otp.matches("\\d{6}")) {
            throw new RuntimeException(
                    "Invalid OTP format. Must be exactly 6 digits.");
        }
    }
}
