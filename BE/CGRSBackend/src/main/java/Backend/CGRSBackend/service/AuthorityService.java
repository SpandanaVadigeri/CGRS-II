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
     * Intentionally strips authorityId and departmentId — authorities cannot
     * reassign ownership or re-route to other departments.
     */
    public GrievanceDto updateStatus(Long grievanceId, UpdateGrievanceRequest request) {
        request.setAuthorityId(null);    // authority cannot reassign to another authority
        request.setDepartmentId(null);   // Phase 3: authority cannot re-route department
        return grievanceService.updateGrievance(grievanceId, request);
    }
}
