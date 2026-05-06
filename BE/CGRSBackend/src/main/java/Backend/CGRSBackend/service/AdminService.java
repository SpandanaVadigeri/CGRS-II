package Backend.CGRSBackend.service;

import Backend.CGRSBackend.dto.AuthorityDto;
import Backend.CGRSBackend.dto.CategoryDto;
import Backend.CGRSBackend.dto.DepartmentDto;
import Backend.CGRSBackend.dto.UserDto;
import Backend.CGRSBackend.entity.Authority;
import Backend.CGRSBackend.entity.Category;
import Backend.CGRSBackend.entity.Department;
import Backend.CGRSBackend.repository.AuthorityRepository;
import Backend.CGRSBackend.repository.CategoryRepository;
import Backend.CGRSBackend.repository.DepartmentRepository;
import Backend.CGRSBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin-only operations: user management, category management, authority listing.
 * Phase 3: added full Department CRUD.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorityRepository authorityRepository;
    private final DepartmentRepository departmentRepository; // Phase 3

    // ── Users ────────────────────────────────────────────────────────────────
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    // ── Categories ───────────────────────────────────────────────────────────
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category already exists: " + dto.getName());
        }
        Category saved = categoryRepository.save(
                Category.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .build());
        return CategoryDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // ── Authorities ──────────────────────────────────────────────────────────
    public List<AuthorityDto> getAllAuthorities() {
        return authorityRepository.findAll().stream()
                .map(a -> AuthorityDto.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .department(a.getDepartment())
                        .email(a.getUser().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public AuthorityDto updateAuthorityDepartment(Long authorityId, String department) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new RuntimeException("Authority not found: " + authorityId));
        authority.setDepartment(department);
        Authority saved = authorityRepository.save(authority);
        return AuthorityDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .department(saved.getDepartment())
                .email(saved.getUser().getEmail())
                .build();
    }

    // ── Departments (Phase 3) ─────────────────────────────────────────────────

    /** GET /departments or /admin/departments — List all registered departments. */
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(d -> DepartmentDto.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    /** POST /admin/departments — Create a new department (name must be unique). */
    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Department already exists: " + dto.getName());
        }
        Department saved = departmentRepository.save(
                Department.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .build());
        return DepartmentDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }

    /** DELETE /admin/departments/{id} — Delete a department by ID. */
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
