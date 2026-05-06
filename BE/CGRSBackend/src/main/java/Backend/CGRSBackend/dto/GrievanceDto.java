package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.GrievancePriority;
import Backend.CGRSBackend.entity.GrievanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for grievance data — hides entity internals.
 * Phase 3: added departmentName for department routing visibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrievanceDto {
    private Long id;
    private String title;
    private String description;
    private GrievanceStatus status;
    private GrievancePriority priority;
    private String categoryName;
    private String citizenName;
    private String citizenEmail;
    private String authorityName;
    private String authorityDepartment;
    /** Phase 3 — Department Routing */
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

