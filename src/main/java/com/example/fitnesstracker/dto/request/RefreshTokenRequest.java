package com.example.fitnesstracker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request DTO for refreshing an authentication token")
public class RefreshTokenRequest {
    @JsonProperty("refresh_token")
    @NotBlank(message = "Refresh token cannot be empty")
    @Schema(description = "A valid, non-expired refresh token obtained during login", example = "some.secret.key...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}