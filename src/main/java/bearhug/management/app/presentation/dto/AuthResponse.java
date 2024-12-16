package bearhug.management.app.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"message", "token", "status"})
public record AuthResponse(
        String message,
        String token,
        boolean status
) {
}
