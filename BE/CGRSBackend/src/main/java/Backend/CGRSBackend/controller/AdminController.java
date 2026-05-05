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
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final GrievanceService grievanceService;

    // ── Users ───────────────────────────────────────────────────────────────

    /** GET /admin/users — List all registered users */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // ── Grievances ──────────────────────────────────────────────────────────

    /** GET /admin/grievances — List all grievances */
    @GetMapping("/grievances")
    public ResponseEntity<List<GrievanceDto>> getAllGrievances() {
        return ResponseEntity.ok(grievanceService.getAllGrievances());
    }

    /** PUT /admin/grievances/{id} — Update status or assign authority */
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

    // ── Categories ──────────────────────────────────────────────────────────

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

    // ── Authorities ─────────────────────────────────────────────────────────

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
}
