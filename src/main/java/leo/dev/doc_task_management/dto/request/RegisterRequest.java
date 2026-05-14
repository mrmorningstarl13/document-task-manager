package leo.dev.doc_task_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can only contain letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name can only contain letters")
    private String lastName;
}
