package com.example.fitnesstracker.dto.response;

import com.example.fitnesstracker.entity.enums.WorkoutType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Response DTO containing the details of a workout")
public class WorkoutResponseDto {
    @Schema(description = "Unique identifier of the workout")
    private Long id;

    @Schema(description = "Name of the workout session", example = "Morning Cardio", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "The date the workout was performed", example = "2025-11-12", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "Duration of the workout in minutes", example = "45", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration;

    @Schema(description = "Estimated calories burned during the workout", example = "350", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer calories;

    @Schema(description = "Optional notes about the workout session", example = "Felt great, good pace.")
    private String notes;

    @Schema(description = "Type of the workout", example = "CARDIO", requiredMode = Schema.RequiredMode.REQUIRED)
    private WorkoutType type;
}