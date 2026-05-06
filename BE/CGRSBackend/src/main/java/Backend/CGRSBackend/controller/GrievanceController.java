package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.DashboardDto;
import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.StatusUpdateDto;
import Backend.CGRSBackend.dto.SubmitGrievanceRequest;
import Backend.CGRSBackend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Grievance endpoints — accessible by all authenticated users.
 * /grievances/** → requires valid JWT
 *
 * Phase 3 additions:
 *   GET /grievances/{id}/history — full status timeline
 *   GET /grievances/dashboard    — role-specific dashboard (citizen view)
 */
@RestController
@RequestMapping("/grievances")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;

    /**
     * POST /grievances
     * Submit a new grievance (CITIZEN).
     * Body: { "title", "description", "priority", "categoryId", "departmentId" }
     */
    @PostMapping
    public ResponseEntity<GrievanceDto> submit(@RequestBody SubmitGrievanceRequest request) {
        return ResponseEntity.ok(grievanceService.submitGrievance(request));
    }

    /**
     * GET /grievances/my
     * Get grievances submitted by the currently logged-in citizen.
     */
    @GetMapping("/my")
    public ResponseEntity<List<GrievanceDto>> getMyGrievances() {
        return ResponseEntity.ok(grievanceService.getMyGrievances());
    }

    /**
     * GET /grievances/{id}
     * Get a specific grievance by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GrievanceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(grievanceService.getGrievanceById(id));
    }

    /**
     * GET /grievances/{id}/history
     * Phase 3 — Status Timeline.
     * Returns the full chronological status-change history for a grievance.
     * Response: [ { id, status, remark, updatedAt }, ... ]  (newest first)
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusUpdateDto>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(grievanceService.getGrievanceHistory(id));
    }

    /**
     * GET /grievances/dashboard
     * Phase 3 — Role-Based Dashboard.
     * Returns a summary dashboard scoped to the current user's role:
     *   CITIZEN   → own grievances + counts
     *   AUTHORITY → department grievances + counts
     *   ADMIN     → all grievances + counts
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(grievanceService.getDashboard());
    }
}
