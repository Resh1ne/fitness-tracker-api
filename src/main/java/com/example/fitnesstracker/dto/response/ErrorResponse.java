package com.example.fitnesstracker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response format")
public class ErrorResponse {
    @Schema(description = "The HTTP status code", example = "404")
    private final int status;

    @Schema(description = "A user-friendly error message", example = "Workout not found with id: 99")
    private final String message;

    @Schema(description = "The timestamp when the error occurred")
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "The path of the request that caused the error", example = "/api/v1/workouts/99")
    private String path;

    @Schema(description = "A map of validation errors, where the key is the field name and the value is the error message")
    private Map<String, String> validationErrors;
}