package com.example.fitnesstracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(description = "DTO for authentication response, containing access and refresh tokens")
public class AuthenticationResponse {
    @JsonProperty("access_token")
    @Schema(description = "Short-lived JWT access token for authenticating requests")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "Long-lived JWT refresh token to obtain a new access token")
    private String refreshToken;
}