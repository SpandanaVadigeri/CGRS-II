package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.SubmitGrievanceRequest;
import Backend.CGRSBackend.dto.UpdateGrievanceRequest;
import Backend.CGRSBackend.entity.*;
import Backend.CGRSBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Core grievance business logic — submit, view, update, delete.
 */
@Service
@RequiredArgsConstructor
public class GrievanceService {

    private final GrievanceRepository grievanceRepository;
    private final CitizenRepository citizenRepository;
    private final AuthorityRepository authorityRepository;
    private final CategoryRepository categoryRepository;
    private final StatusUpdateRepository statusUpdateRepository;
    private final UserRepository userRepository;

    // ── Helper: get email of currently authenticated user ──────────────────
    private String getCurrentEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails ud) return ud.getUsername();
        return principal.toString();
    }

    // ── Helper: resolve Citizen from auth context ───────────────────────────
    private Citizen getCurrentCitizen() {
        String email = getCurrentEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return citizenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Citizen profile not found for: " + email));
    }

    // ── Helper: map Grievance → GrievanceDto ───────────────────────────────
    private GrievanceDto toDto(Grievance g) {
        return GrievanceDto.builder()
                .id(g.getId())
                .title(g.getTitle())
                .description(g.getDescription())
                .status(g.getStatus())
                .priority(g.getPriority())
                .categoryName(g.getCategory() != null ? g.getCategory().getName() : null)
                .citizenName(g.getCitizen().getUser().getName())
                .citizenEmail(g.getCitizen().getUser().getEmail())
                .authorityName(g.getAuthority() != null ? g.getAuthority().getName() : null)
                .authorityDepartment(g.getAuthority() != null ? g.getAuthority().getDepartment() : null)
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }

    // ── Submit grievance (CITIZEN) ──────────────────────────────────────────
    public GrievanceDto submitGrievance(SubmitGrievanceRequest request) {
        Citizen citizen = getCurrentCitizen();

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
        }

        GrievancePriority priority = request.getPriority() != null
                ? request.getPriority() : GrievancePriority.MEDIUM;

        Grievance grievance = Grievance.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .status(GrievanceStatus.PENDING)
                .category(category)
                .citizen(citizen)
                .build();

        Grievance saved = grievanceRepository.save(grievance);

        // Log initial status
        statusUpdateRepository.save(StatusUpdate.builder()
                .grievance(saved)
                .status(GrievanceStatus.PENDING)
                .remark("Grievance submitted")
                .build());

        return toDto(saved);
    }

    // ── Get my grievances (CITIZEN) ─────────────────────────────────────────
    public List<GrievanceDto> getMyGrievances() {
        Citizen citizen = getCurrentCitizen();
        return grievanceRepository.findByCitizenOrderByCreatedAtDesc(citizen)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Get all grievances (ADMIN) ──────────────────────────────────────────
    public List<GrievanceDto> getAllGrievances() {
        return grievanceRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── Get one grievance by ID ─────────────────────────────────────────────
    public GrievanceDto getGrievanceById(Long id) {
        return toDto(grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id)));
    }

    // ── Update status / assign authority (ADMIN / AUTHORITY) ───────────────
    public GrievanceDto updateGrievance(Long id, UpdateGrievanceRequest request) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id));

        if (request.getStatus() != null) {
            grievance.setStatus(request.getStatus());
        }

        if (request.getAuthorityId() != null) {
            Authority authority = authorityRepository.findById(request.getAuthorityId())
                    .orElseThrow(() -> new RuntimeException("Authority not found: " + request.getAuthorityId()));
            grievance.setAuthority(authority);
        }

        Grievance updated = grievanceRepository.save(grievance);

        // Append status log entry
        if (request.getStatus() != null) {
            statusUpdateRepository.save(StatusUpdate.builder()
                    .grievance(updated)
                    .status(request.getStatus())
                    .remark(request.getRemark() != null ? request.getRemark() : "Status updated")
                    .build());
        }

        return toDto(updated);
    }

    // ── Delete grievance (ADMIN) ────────────────────────────────────────────
    public void deleteGrievance(Long id) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id));
        grievanceRepository.delete(grievance);
    }

    // ── Get grievances assigned to current authority ────────────────────────
    public List<GrievanceDto> getAssignedGrievances() {
        String email = getCurrentEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        Authority authority = authorityRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Authority profile not found for: " + email));
        return grievanceRepository.findByAuthority(authority)
                .stream().map(this::toDto).collect(Collectors.toList());
    }
}
