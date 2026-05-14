package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.Future;
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
    private String priority;
    private String status;
    @Future(message = "Deadline must be a future date")
    private LocalDateTime deadline;
    private Long assignedToId;
}
