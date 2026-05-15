package leo.dev.doc_task_management.controller;

import jakarta.validation.Valid;
import leo.dev.doc_task_management.dto.request.CreateTaskRequest;
import leo.dev.doc_task_management.dto.request.UpdateTaskRequest;
import leo.dev.doc_task_management.dto.response.TaskResponse;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@AuthenticationPrincipal User currentUser,
                                                   @PathVariable Long projectId,
                                                   @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(currentUser, projectId, request));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@AuthenticationPrincipal User currentUser,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long taskId,
                                                   @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(currentUser, projectId, taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal User currentUser,
                                           @PathVariable Long projectId,
                                           @PathVariable Long taskId) {
        taskService.deleteTask(currentUser, projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@AuthenticationPrincipal User currentUser,
                                                              @PathVariable Long projectId,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(taskService.getProjectTasks(currentUser, projectId, status, priority));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getMyTasks(currentUser));
    }
}
