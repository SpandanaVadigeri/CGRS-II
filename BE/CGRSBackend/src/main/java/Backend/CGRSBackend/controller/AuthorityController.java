package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.DashboardDto;
import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.UpdateGrievanceRequest;
import Backend.CGRSBackend.service.AuthorityService;
import Backend.CGRSBackend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authority-only endpoints — protected by ROLE_AUTHORITY in SecurityConfig.
 * /authority/** → requires JWT + AUTHORITY role
 *
 * Phase 3 additions:
 *   GET /authority/dashboard — authority-scoped dashboard
 */
@RestController
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;
    private final GrievanceService grievanceService;

    /**
     * GET /authority/grievances
     * View all grievances assigned to the logged-in authority.
     * Phase 3: also includes unassigned grievances in the authority's department.
     */
    @GetMapping("/grievances")
    public ResponseEntity<List<GrievanceDto>> getAssignedGrievances() {
        return ResponseEntity.ok(authorityService.getMyAssignedGrievances());
    }

    /**
     * PUT /authority/grievances/{id}
     * Update status of a grievance with an optional remark.
     * Body: { "status": "IN_PROGRESS", "remark": "Under investigation" }
     * Note: authorityId and departmentId fields are ignored for this role.
     */
    @PutMapping("/grievances/{id}")
    public ResponseEntity<GrievanceDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateGrievanceRequest request) {
        return ResponseEntity.ok(authorityService.updateStatus(id, request));
    }

    /**
     * GET /authority/dashboard
     * Phase 3 — Role-Based Dashboard.
     * Returns grievance counts and recent list scoped to this authority's department.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(grievanceService.getDashboard());
    }
}
