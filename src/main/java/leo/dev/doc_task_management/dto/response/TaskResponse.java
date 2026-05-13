package leo.dev.doc_task_management.dto.response;

import leo.dev.doc_task_management.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime deadline;
    private UserResponse assignedTo;
    private UserResponse createdBy;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .deadline(task.getDeadline())
                .assignedTo(task.getAssignedTo() != null
                        ? UserResponse.fromEntity(task.getAssignedTo())
                        : null)
                .createdBy(UserResponse.fromEntity(task.getCreatedBy()))
                .projectId(task.getProject().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
