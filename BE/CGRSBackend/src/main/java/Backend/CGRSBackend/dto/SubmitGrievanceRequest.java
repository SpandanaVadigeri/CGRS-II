package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.GrievancePriority;
import lombok.Data;

/**
 * Request body for submitting a new grievance (used by CITIZEN).
 */
@Data
public class SubmitGrievanceRequest {
    private String title;
    private String description;
    private GrievancePriority priority;
    private Long categoryId; // optional
}
