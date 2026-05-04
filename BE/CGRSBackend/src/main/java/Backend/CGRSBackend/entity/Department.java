package Backend.CGRSBackend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a government department in the CGRS system.
 * Basic structure for Phase 1 - can be extended later.
 */
@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
