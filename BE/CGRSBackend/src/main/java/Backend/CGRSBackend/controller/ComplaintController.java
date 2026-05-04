package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.ComplaintRequest;
import Backend.CGRSBackend.entity.Complaint;
import Backend.CGRSBackend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles all complaint CRUD operations.
 * All endpoints are protected — a valid JWT token is required.
 */
@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    /**
     * POST /complaints
     * Create a new complaint (logged-in user is the owner).
     */
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.createComplaint(request));
    }

    /**
     * GET /complaints
     * Retrieve all complaints in the system.
     */
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    /**
     * GET /complaints/{id}
     * Retrieve a specific complaint by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    /**
     * PUT /complaints/{id}
     * Update a complaint's title, description, category, or status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id,
                                                     @RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, request));
    }

    /**
     * DELETE /complaints/{id}
     * Delete a complaint by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}
