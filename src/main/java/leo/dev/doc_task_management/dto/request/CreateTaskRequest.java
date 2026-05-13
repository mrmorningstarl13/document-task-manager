package leo.dev.doc_task_management.dto.request;

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
    private String title;
    private String description;
    private String priority;
    private LocalDateTime deadline;
    private Long assignedToId;
}