package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.DashboardDto;
import Backend.CGRSBackend.dto.GrievanceDto;
import Backend.CGRSBackend.dto.StatusUpdateDto;
import Backend.CGRSBackend.dto.SubmitGrievanceRequest;
import Backend.CGRSBackend.dto.UpdateGrievanceRequest;
import Backend.CGRSBackend.entity.*;
import Backend.CGRSBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Core grievance business logic.
 * Phase 3: department routing, status timeline, role-based dashboards,
 *          and notification triggers added.
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
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;  // Phase 3

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String getCurrentEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails ud) return ud.getUsername();
        return principal.toString();
    }

    private User getCurrentUser() {
        String email = getCurrentEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private Citizen getCurrentCitizen() {
        User user = getCurrentUser();
        return citizenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Citizen profile not found for: " + user.getEmail()));
    }

    /** Map Grievance entity → GrievanceDto (includes Phase 3 departmentName). */
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
                .departmentName(g.getDepartment() != null ? g.getDepartment().getName() : null) // Phase 3
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }

    /** Map StatusUpdate → StatusUpdateDto for timeline responses. */
    private StatusUpdateDto toStatusDto(StatusUpdate s) {
        return StatusUpdateDto.builder()
                .id(s.getId())
                .status(s.getStatus())
                .remark(s.getRemark())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    // ── Feature 1: Department Routing ──────────────────────────────────────────

    /**
     * POST /grievances
     * Submit a new grievance. Optionally routes it to a department on creation.
     */
    @Transactional
    public GrievanceDto submitGrievance(SubmitGrievanceRequest request) {
        Citizen citizen = getCurrentCitizen();

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
        }

        // Phase 3 — resolve department if provided
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + request.getDepartmentId()));
        }

        GrievancePriority priority = request.getPriority() != null
                ? request.getPriority() : GrievancePriority.MEDIUM;

        Grievance grievance = Grievance.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .status(GrievanceStatus.PENDING)
                .category(category)
                .department(department)   // Phase 3
                .citizen(citizen)
                .build();

        Grievance saved = grievanceRepository.save(grievance);

        // Phase 2: initial timeline entry
        statusUpdateRepository.save(StatusUpdate.builder()
                .grievance(saved)
                .status(GrievanceStatus.PENDING)
                .remark("Grievance submitted")
                .build());

        // Phase 3: trigger notification
        notificationService.notifyGrievanceSubmitted(saved);

        return toDto(saved);
    }

    // ── Feature 2: Status Timeline ─────────────────────────────────────────────

    /**
     * GET /grievances/{id}/history
     * Returns the full status-change timeline for a grievance, newest first.
     */
    public List<StatusUpdateDto> getGrievanceHistory(Long grievanceId) {
        Grievance grievance = grievanceRepository.findById(grievanceId)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + grievanceId));
        return statusUpdateRepository.findByGrievanceOrderByUpdatedAtDesc(grievance)
                .stream().map(this::toStatusDto).collect(Collectors.toList());
    }

    // ── Feature 3: Role-Based Dashboards ──────────────────────────────────────

    /**
     * Build a role-specific dashboard for the currently authenticated user.
     * CITIZEN   → own grievances only
     * AUTHORITY → grievances in their department
     * ADMIN     → all grievances
     */
    public DashboardDto getDashboard() {
        User user = getCurrentUser();
        String email = user.getEmail();
        String role  = user.getRole().name();

        List<GrievanceDto> grievances = switch (user.getRole()) {
            case CITIZEN   -> getMyGrievances();
            case AUTHORITY -> getAssignedGrievances();
            case ADMIN     -> getAllGrievances();
        };

        long total      = grievances.size();
        long pending    = grievances.stream().filter(g -> g.getStatus() == GrievanceStatus.PENDING).count();
        long inProgress = grievances.stream().filter(g -> g.getStatus() == GrievanceStatus.IN_PROGRESS).count();
        long resolved   = grievances.stream().filter(g -> g.getStatus() == GrievanceStatus.RESOLVED).count();
        long escalated  = grievances.stream().filter(g -> g.getStatus() == GrievanceStatus.ESCALATED).count();

        // Show the 5 most recent for the dashboard preview
        List<GrievanceDto> recent = grievances.stream().limit(5).collect(Collectors.toList());

        return DashboardDto.builder()
                .userEmail(email)
                .userRole(role)
                .totalGrievances(total)
                .pendingCount(pending)
                .inProgressCount(inProgress)
                .resolvedCount(resolved)
                .escalatedCount(escalated)
                .recentGrievances(recent)
                .build();
    }

    // ── CRUD ───────────────────────────────────────────────────────────────────

    /** GET /grievances/my — CITIZEN: own grievances */
    public List<GrievanceDto> getMyGrievances() {
        Citizen citizen = getCurrentCitizen();
        return grievanceRepository.findByCitizenOrderByCreatedAtDesc(citizen)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** GET /admin/grievances — ADMIN: all grievances */
    public List<GrievanceDto> getAllGrievances() {
        return grievanceRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** GET /grievances/{id} */
    public GrievanceDto getGrievanceById(Long id) {
        return toDto(grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id)));
    }

    /**
     * PUT /admin/grievances/{id} or PUT /authority/grievances/{id}
     * Handles status change, authority assignment, and department routing.
     * Phase 3: department re-routing + notification on status change.
     */
    @Transactional
    public GrievanceDto updateGrievance(Long id, UpdateGrievanceRequest request) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id));

        boolean statusChanged = false;

        if (request.getStatus() != null) {
            grievance.setStatus(request.getStatus());
            statusChanged = true;
        }

        if (request.getAuthorityId() != null) {
            Authority authority = authorityRepository.findById(request.getAuthorityId())
                    .orElseThrow(() -> new RuntimeException("Authority not found: " + request.getAuthorityId()));
            grievance.setAuthority(authority);
        }

        // Phase 3 — department re-routing
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found: " + request.getDepartmentId()));
            grievance.setDepartment(department);
        }

        Grievance updated = grievanceRepository.save(grievance);

        // Append timeline entry if status changed
        if (statusChanged) {
            String remark = request.getRemark() != null ? request.getRemark() : "Status updated";
            statusUpdateRepository.save(StatusUpdate.builder()
                    .grievance(updated)
                    .status(request.getStatus())
                    .remark(remark)
                    .build());

            // Phase 3 — notify citizen of the status change
            notificationService.notifyStatusChanged(updated, request.getStatus(), remark);
        }

        return toDto(updated);
    }

    /** DELETE /admin/grievances/{id} */
    public void deleteGrievance(Long id) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found: " + id));
        grievanceRepository.delete(grievance);
    }

    /**
     * AUTHORITY: get grievances assigned to the logged-in authority.
     * Phase 3: also includes department-based fallback.
     */
    public List<GrievanceDto> getAssignedGrievances() {
        User user = getCurrentUser();
        Authority authority = authorityRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Authority profile not found for: " + user.getEmail()));

        // Primary: grievances directly assigned to this authority
        List<Grievance> byAuthority = grievanceRepository.findByAuthority(authority);

        // Phase 3: also pull unassigned grievances in the authority's department
        // (department name stored as String on Authority entity)
        List<Grievance> byDept = departmentRepository.findByName(authority.getDepartment())
                .map(grievanceRepository::findByDepartmentOrderByCreatedAtDesc)
                .orElse(List.of());

        // Merge, de-duplicate by ID
        return java.util.stream.Stream.concat(byAuthority.stream(), byDept.stream())
                .collect(Collectors.toMap(Grievance::getId, g -> g, (a, b) -> a))
                .values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

