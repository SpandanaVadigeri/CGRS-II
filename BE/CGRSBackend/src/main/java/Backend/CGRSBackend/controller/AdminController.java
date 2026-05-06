package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.*;
import Backend.CGRSBackend.service.AdminService;
import Backend.CGRSBackend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-only endpoints — protected by ROLE_ADMIN in SecurityConfig.
 * /admin/** → requires JWT + ADMIN role
 *
 * Phase 3 additions:
 *   POST   /admin/departments          — create department
 *   GET    /admin/departments          — list all departments
 *   DELETE /admin/departments/{id}     — delete department
 *   GET    /admin/dashboard            — role-based admin dashboard
 *   PUT    /admin/grievances/{id}      — now also supports departmentId routing
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final GrievanceService grievanceService;

    // ── Users ────────────────────────────────────────────────────────────────

    /** GET /admin/users — List all registered users */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // ── Grievances ───────────────────────────────────────────────────────────

    /** GET /admin/grievances — List all grievances (admin view) */
    @GetMapping("/grievances")
    public ResponseEntity<List<GrievanceDto>> getAllGrievances() {
        return ResponseEntity.ok(grievanceService.getAllGrievances());
    }

    /**
     * PUT /admin/grievances/{id}
     * Update status, assign authority, or re-route to a department.
     * Body: { "status": "IN_PROGRESS", "authorityId": 2, "departmentId": 1, "remark": "..." }
     */
    @PutMapping("/grievances/{id}")
    public ResponseEntity<GrievanceDto> updateGrievance(
            @PathVariable Long id,
            @RequestBody UpdateGrievanceRequest request) {
        return ResponseEntity.ok(grievanceService.updateGrievance(id, request));
    }

    /** DELETE /admin/grievances/{id} — Delete a grievance */
    @DeleteMapping("/grievances/{id}")
    public ResponseEntity<String> deleteGrievance(@PathVariable Long id) {
        grievanceService.deleteGrievance(id);
        return ResponseEntity.ok("Grievance deleted successfully");
    }

    // ── Categories ───────────────────────────────────────────────────────────

    /** GET /admin/categories — List all categories */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    /** POST /admin/categories — Create a new category */
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(adminService.createCategory(dto));
    }

    /** DELETE /admin/categories/{id} — Delete a category */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted");
    }

    // ── Authorities ──────────────────────────────────────────────────────────

    /** GET /admin/authorities — List all authorities */
    @GetMapping("/authorities")
    public ResponseEntity<List<AuthorityDto>> getAllAuthorities() {
        return ResponseEntity.ok(adminService.getAllAuthorities());
    }

    /** PUT /admin/authorities/{id}/department — Update authority's department */
    @PutMapping("/authorities/{id}/department")
    public ResponseEntity<AuthorityDto> updateDepartment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.updateAuthorityDepartment(id, body.get("department")));
    }

    // ── Departments (Phase 3) ─────────────────────────────────────────────────

    /**
     * POST /admin/departments
     * Create a new department.
     * Body: { "name": "Road & Infrastructure", "description": "Manages road complaints" }
     */
    @PostMapping("/departments")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto dto) {
        return ResponseEntity.ok(adminService.createDepartment(dto));
    }

    /**
     * GET /admin/departments
     * List all departments (admin view — same data as public /departments).
     */
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    /**
     * DELETE /admin/departments/{id}
     * Delete a department by ID.
     */
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        adminService.deleteDepartment(id);
        return ResponseEntity.ok("Department deleted successfully");
    }

    // ── Dashboard (Phase 3) ───────────────────────────────────────────────────

    /**
     * GET /admin/dashboard
     * Returns admin-scoped dashboard: all grievances + summary counts.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(grievanceService.getDashboard());
    }
}
