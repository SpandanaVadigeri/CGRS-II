package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.GrievanceStatus;
import lombok.Data;

/**
 * Request body for updating a grievance (used by ADMIN / AUTHORITY).
 * Phase 3: added departmentId for department re-routing by admin.
 */
@Data
public class UpdateGrievanceRequest {
    private GrievanceStatus status;
    private Long authorityId;  // assign/reassign authority (admin only)
    private Long departmentId; // re-route to department (admin only) — Phase 3
    private String remark;     // note added with the status change
}

