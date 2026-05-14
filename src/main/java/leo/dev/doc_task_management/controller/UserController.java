package leo.dev.doc_task_management.controller;

import jakarta.validation.Valid;
import leo.dev.doc_task_management.dto.request.ChangeRoleRequest;
import leo.dev.doc_task_management.dto.request.UpdateUserRequest;
import leo.dev.doc_task_management.dto.response.UserResponse;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.service.ProjectService;
import leo.dev.doc_task_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getProfile(currentUser));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal User currentUser,
                                                      @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    @GetMapping("/me/projects")
    public ResponseEntity<?> getMyProjects(@AuthenticationPrincipal User currentUser) {
        return (ResponseEntity<?>) projectService.getMyProjects(currentUser);
    }

    // ADMIN endpoints
    @GetMapping("/admin/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/admin/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long id,
                                                       @Valid @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(userService.changeUserRole(id, request.getRole()));
    }

    @PutMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@AuthenticationPrincipal User currentUser,
                                                       @PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id, currentUser));
    }
}