package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.request.CreateTaskRequest;
import leo.dev.doc_task_management.dto.request.UpdateTaskRequest;
import leo.dev.doc_task_management.dto.response.TaskResponse;
import leo.dev.doc_task_management.entity.*;
import leo.dev.doc_task_management.exception.ForbiddenOperationException;
import leo.dev.doc_task_management.exception.ProjectNotFoundException;
import leo.dev.doc_task_management.exception.TaskNotFoundException;
import leo.dev.doc_task_management.exception.UserNotFoundException;
import leo.dev.doc_task_management.repository.ProjectRepository;
import leo.dev.doc_task_management.repository.TaskRepository;
import leo.dev.doc_task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskResponse createTask(User currentUser, Long projectId, CreateTaskRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        if (project.getOwner().getId() != currentUser.getId() &&
                !project.getMembers().contains(currentUser)) {
            throw new ForbiddenOperationException("You are not a member of this project");
        }

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getAssignedToId()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(TaskPriority.valueOf(request.getPriority().toUpperCase()))
                .status(TaskStatus.NEW)
                .deadline(request.getDeadline())
                .assignedTo(assignedTo)
                .createdBy(currentUser)
                .project(project)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse updateTask(User currentUser, Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (task.getCreatedBy().getId() != currentUser.getId() &&
                task.getProject().getOwner().getId() != currentUser.getId() &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenOperationException("You don't have permission to update this task");
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
        if (request.getStatus() != null) task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    public void deleteTask(User currentUser, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (task.getCreatedBy().getId() != currentUser.getId() &&
                task.getProject().getOwner().getId() != currentUser.getId() &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenOperationException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    public List<TaskResponse> getProjectTasks(User currentUser, Long projectId,
                                              String status, String priority) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        if (project.getOwner().getId() != currentUser.getId() &&
                !project.getMembers().contains(currentUser)) {
            throw new ForbiddenOperationException("You are not a member of this project");
        }

        if (status != null && priority != null) {
            return taskRepository.findAllByProjectAndStatusAndPriority(
                            project,
                            TaskStatus.valueOf(status.toUpperCase()),
                            TaskPriority.valueOf(priority.toUpperCase()))
                    .stream().map(TaskResponse::fromEntity).toList();
        } else if (status != null) {
            return taskRepository.findAllByProjectAndStatus(
                            project,
                            TaskStatus.valueOf(status.toUpperCase()))
                    .stream().map(TaskResponse::fromEntity).toList();
        } else if (priority != null) {
            return taskRepository.findAllByProjectAndPriority(
                            project,
                            TaskPriority.valueOf(priority.toUpperCase()))
                    .stream().map(TaskResponse::fromEntity).toList();
        }

        return taskRepository.findAllByProject(project)
                .stream().map(TaskResponse::fromEntity).toList();
    }

    public List<TaskResponse> getMyTasks(User currentUser) {
        return taskRepository.findAllByAssignedTo(currentUser)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}