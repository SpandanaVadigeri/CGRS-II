package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.AuthResponse;
import Backend.CGRSBackend.dto.LoginRequest;
import Backend.CGRSBackend.dto.RegisterRequest;
import Backend.CGRSBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles public authentication endpoints.
 * These routes are NOT protected — anyone can register or login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * POST /auth/register
     * Register a new user and get a JWT token.
     *
     * Request body: { name, email, password, role (optional) }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/login
     * Login with email and password, receive a JWT token.
     *
     * Request body: { email, password }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
