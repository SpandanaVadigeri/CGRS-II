package Backend.CGRSBackend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a registered user of the CGRS system.
 * Implements UserDetails indirectly through UserService.
 * Aadhaar OTP login: aadhaarNumber is optional but must be unique when set.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Aadhaar number — optional, 12-digit, unique per user.
     * Required for OTP-based login. Nullable so existing accounts are unaffected.
     */
    @Column(unique = true)
    private String aadhaarNumber;
}

