package com.example.fitnesstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Paginated response for a list of workouts")
public class WorkoutPageResponseDto {
    @Schema(description = "The list of workout DTOs for the current page")
    private List<WorkoutResponseDto> content;

    @Schema(description = "Total number of workouts matching the filter criteria", example = "150")
    private long totalElements;

    @Schema(description = "Total number of pages available", example = "15")
    private int totalPages;

    @Schema(description = "The current page number (zero-based)", example = "0")
    private int page;

    @Schema(description = "The number of workouts per page", example = "10")
    private int size;
}