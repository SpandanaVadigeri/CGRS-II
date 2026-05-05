package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.GrievanceStatus;
import lombok.Data;

/**
 * Request body for updating a grievance (used by ADMIN / AUTHORITY).
 */
@Data
public class UpdateGrievanceRequest {
    private GrievanceStatus status;
    private Long authorityId; // assign/reassign authority (admin only)
    private String remark;    // note added with the status change
}
