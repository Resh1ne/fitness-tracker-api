package com.example.fitnesstracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @JsonProperty("refresh_token")
    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}