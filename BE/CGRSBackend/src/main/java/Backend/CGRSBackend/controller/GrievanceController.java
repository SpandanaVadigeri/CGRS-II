package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.SubmitGrievanceRequest;
import Backend.CGRSBackend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Grievance endpoints — accessible by authenticated citizens.
 * /grievances/** → requires valid JWT
 */
@RestController
@RequestMapping("/grievances")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;

    /**
     * POST /grievances
     * Submit a new grievance (CITIZEN only).
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
}
