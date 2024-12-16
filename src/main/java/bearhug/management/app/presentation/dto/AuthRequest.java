package bearhug.management.app.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthRequest(
        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "password is required") @Size(min = 8)
        String password
) {
}
