package Backend.CGRSBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Role-based dashboard summary DTO.
 *
 * Citizen  → sees only their own grievance counts.
 * Authority → sees counts for grievances assigned to their department.
 * Admin    → sees system-wide counts + full grievance list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {

    /** Identity of the logged-in user */
    private String userEmail;
    private String userRole;

    // ── Summary counts ────────────────────────────────────────────────────────
    private long totalGrievances;
    private long pendingCount;
    private long inProgressCount;
    private long resolvedCount;
    private long escalatedCount;

    /** Most recent / relevant grievances (size depends on role) */
    private List<GrievanceDto> recentGrievances;
}
