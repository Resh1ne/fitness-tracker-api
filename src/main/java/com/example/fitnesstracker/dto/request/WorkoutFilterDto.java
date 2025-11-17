package com.example.fitnesstracker.dto.request;

import com.example.fitnesstracker.entity.enums.WorkoutType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "DTO with filter parameters for searching workouts")
public class WorkoutFilterDto {

    @Schema(description = "Filter by a specific workout type.", example = "CARDIO")
    private WorkoutType type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "The start date for the search range (inclusive). Format: YYYY-MM-DD", example = "2025-01-01")
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "The end date for the search range (inclusive). Format: YYYY-MM-DD", example = "2025-12-31")
    private LocalDate dateTo;

    @Schema(description = "Minimum duration of the workout in minutes.", example = "30")
    private Integer durationFrom;

    @Schema(description = "Maximum duration of the workout in minutes.", example = "90")
    private Integer durationTo;
}
