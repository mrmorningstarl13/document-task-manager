package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT", message = "Priority must be LOW, MEDIUM, HIGH or URGENT")
    private String priority;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be a future date")
    private LocalDateTime deadline;

    private Long assignedToId;
}