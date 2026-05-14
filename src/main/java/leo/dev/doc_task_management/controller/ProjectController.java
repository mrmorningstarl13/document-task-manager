package leo.dev.doc_task_management.controller;

import jakarta.validation.Valid;
import leo.dev.doc_task_management.dto.request.AddMemberRequest;
import leo.dev.doc_task_management.dto.request.CreateProjectRequest;
import leo.dev.doc_task_management.dto.request.UpdateProjectRequest;
import leo.dev.doc_task_management.dto.response.ProjectResponse;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@AuthenticationPrincipal User currentUser,
                                                         @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(currentUser, request));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectResponse> addMember(@AuthenticationPrincipal User currentUser,
                                                     @PathVariable Long projectId,
                                                     @RequestBody AddMemberRequest request) {
        return ResponseEntity.ok(projectService.addMember(currentUser, projectId, request));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@AuthenticationPrincipal User currentUser,
                                                         @PathVariable Long projectId,
                                                         @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(currentUser, projectId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "false") boolean includeDeleted
    ) {
        return ResponseEntity.ok(projectService.getAllProjects(includeDeleted));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.getMyProjects(currentUser));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal User currentUser,
                                              @PathVariable Long projectId) {
        projectService.deleteProject(currentUser, projectId);
        return ResponseEntity.noContent().build();
    }
}