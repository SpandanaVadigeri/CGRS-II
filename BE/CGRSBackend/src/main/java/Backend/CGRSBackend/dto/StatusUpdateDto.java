package Backend.CGRSBackend.dto;

import Backend.CGRSBackend.entity.GrievanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for a single status timeline entry.
 * Returned by GET /grievances/{id}/history.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateDto {
    private Long id;
    private GrievanceStatus status;
    private String remark;
    private LocalDateTime updatedAt;
}
