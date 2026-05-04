package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.ComplaintStatus;
import lombok.Data;

/**
 * Incoming request body for creating or updating a complaint.
 */
@Data
public class ComplaintRequest {

    private String title;
    private String description;
    private String category;

    // Used only on update; ignored on create (defaults to PENDING)
    private ComplaintStatus status;
}
