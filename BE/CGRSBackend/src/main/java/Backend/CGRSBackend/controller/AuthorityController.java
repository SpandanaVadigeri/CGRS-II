package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.UpdateGrievanceRequest;
import Backend.CGRSBackend.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authority-only endpoints — protected by ROLE_AUTHORITY in SecurityConfig.
 * /authority/** → requires JWT + AUTHORITY role
 */
@RestController
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    /**
     * GET /authority/grievances
     * View all grievances assigned to the logged-in authority.
     */
    @GetMapping("/grievances")
    public ResponseEntity<List<GrievanceDto>> getAssignedGrievances() {
        return ResponseEntity.ok(authorityService.getMyAssignedGrievances());
    }

    /**
     * PUT /authority/grievances/{id}
     * Update status of an assigned grievance (with optional remark).
     * Body: { "status": "IN_PROGRESS", "remark": "Under investigation" }
     */
    @PutMapping("/grievances/{id}")
    public ResponseEntity<GrievanceDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateGrievanceRequest request) {
        return ResponseEntity.ok(authorityService.updateStatus(id, request));
    }
}
