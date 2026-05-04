package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.AuthResponse;
import Backend.CGRSBackend.dto.LoginRequest;
import Backend.CGRSBackend.dto.RegisterRequest;
import Backend.CGRSBackend.entity.Role;
import Backend.CGRSBackend.entity.User;
import Backend.CGRSBackend.repository.UserRepository;
import Backend.CGRSBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user registration and login logic.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user.
     * Defaults role to CITIZEN if not provided.
     * Returns a JWT token on success.
     */
    public AuthResponse register(RegisterRequest request) {
        // Check for existing account
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Determine role — default to CITIZEN
        Role role = (request.getRole() != null) ? request.getRole() : Role.CITIZEN;

        // Build and save user with hashed password
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        // Generate token and return auth response
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    /**
     * Authenticate an existing user.
     * Returns a JWT token on success.
     */
    public AuthResponse login(LoginRequest request) {
        // Look up user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate and return JWT
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
