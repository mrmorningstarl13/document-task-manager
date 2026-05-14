package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequest {
    private String name;
    private String description;

    @Pattern(regexp = "ACTIVE|ARCHIVED", message = "Status must be ACTIVE or ARCHIVED")
    private String status;
}