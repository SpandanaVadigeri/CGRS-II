package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.UpdateGrievanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Authority-specific operations — delegates to GrievanceService.
 */
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final GrievanceService grievanceService;

    /**
     * Get all grievances assigned to the currently logged-in authority.
     */
    public List<GrievanceDto> getMyAssignedGrievances() {
        return grievanceService.getAssignedGrievances();
    }

    /**
     * Update the status of a grievance (AUTHORITY can change status and add remarks).
     */
    public GrievanceDto updateStatus(Long grievanceId, UpdateGrievanceRequest request) {
        // Authority cannot reassign to another authority — null out authorityId
        request.setAuthorityId(null);
        return grievanceService.updateGrievance(grievanceId, request);
    }
}
