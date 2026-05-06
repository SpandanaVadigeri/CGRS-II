package Backend.CGRSBackend.controller;

import Backend.CGRSBackend.dto.DepartmentDto;
import Backend.CGRSBackend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public department listing endpoint — no admin role required.
 * Used by citizens when submitting a grievance to select a department.
 *
 * Security: /departments/** → authenticated (any role), per SecurityConfig.
 */
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final AdminService adminService;

    /**
     * GET /departments
     * List all available departments.
     * Accessible to all authenticated users (CITIZEN, AUTHORITY, ADMIN).
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }
}
