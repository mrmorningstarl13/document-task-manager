package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;

    @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT", message = "Priority must be LOW, MEDIUM, HIGH or URGENT")
    private String priority;

    @Pattern(regexp = "TODO|IN_PROGRESS|DONE", message = "Status must be TODO, IN_PROGRESS or DONE")
    private String status;

    @Future(message = "Deadline must be a future date")
    private LocalDateTime deadline;

    @Positive(message = "User ID must be a positive value")
    private Long assignedToId;
}
