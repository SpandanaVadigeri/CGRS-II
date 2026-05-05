package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.*;
import Backend.CGRSBackend.entity.*;
import Backend.CGRSBackend.repository.*;
import Backend.CGRSBackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles user registration, login, and user data queries.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CitizenRepository citizenRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user.
     * ✔ Creates Citizen/Authority profile based on role.
     * ✘ Does NOT return a JWT token.
     */
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role role = (request.getRole() != null) ? request.getRole() : Role.CITIZEN;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        // Auto-create linked profile based on role
        if (role == Role.CITIZEN) {
            citizenRepository.save(Citizen.builder().user(user).build());
        } else if (role == Role.AUTHORITY) {
            authorityRepository.save(Authority.builder()
                    .name(user.getName())
                    .department("General") // default; can be updated by admin
                    .user(user)
                    .build());
        }

        return new RegisterResponse(
                "User registered successfully",
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Authenticate and return JWT token.
     * ✔ This is the ONLY place a JWT is generated.
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getRole().name());
    }

    /**
     * Get all users as safe DTOs (no passwords).
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }
}
