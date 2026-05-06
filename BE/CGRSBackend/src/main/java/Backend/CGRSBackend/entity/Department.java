package Backend.CGRSBackend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a government department in the CGRS system.
 * Phase 3: added description field and used as FK in Grievance.
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

    /** Optional description of the department's scope/jurisdiction. */
    private String description;
}

