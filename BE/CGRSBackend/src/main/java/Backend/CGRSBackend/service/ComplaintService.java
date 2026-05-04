package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.ComplaintRequest;
import Backend.CGRSBackend.entity.Complaint;
import Backend.CGRSBackend.entity.ComplaintStatus;
import Backend.CGRSBackend.entity.User;
import Backend.CGRSBackend.repository.ComplaintRepository;
import Backend.CGRSBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles all complaint-related business logic.
 */
@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    /**
     * Resolve the currently authenticated user from the SecurityContext.
     */
    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    /**
     * Create a new complaint for the logged-in user.
     * Status defaults to PENDING.
     */
    public Complaint createComplaint(ComplaintRequest request) {
        User user = getCurrentUser();

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(ComplaintStatus.PENDING)
                .user(user)
                .build();

        return complaintRepository.save(complaint);
    }

    /**
     * Get all complaints in the system.
     * Admins and Authorities will use this to see all submissions.
     */
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    /**
     * Get a specific complaint by its ID.
     */
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    /**
     * Update a complaint's fields.
     * If status is provided in the request, it is updated as well.
     */
    public Complaint updateComplaint(Long id, ComplaintRequest request) {
        Complaint complaint = getComplaintById(id);

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());

        // Only update status if explicitly provided
        if (request.getStatus() != null) {
            complaint.setStatus(request.getStatus());
        }

        return complaintRepository.save(complaint);
    }

    /**
     * Delete a complaint by its ID.
     */
    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id); // throws if not found
        complaintRepository.delete(complaint);
    }
}
