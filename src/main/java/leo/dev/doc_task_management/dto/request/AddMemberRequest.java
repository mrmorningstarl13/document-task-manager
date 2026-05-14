package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
}