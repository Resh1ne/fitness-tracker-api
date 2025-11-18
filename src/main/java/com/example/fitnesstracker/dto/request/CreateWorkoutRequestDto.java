package com.example.fitnesstracker.dto.request;

import com.example.fitnesstracker.entity.enums.WorkoutType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request DTO for creating a new workout")
public class CreateWorkoutRequestDto {
    @NotBlank(message = "Workout name cannot be empty")
    @Size(max = 100, message = "Workout name must be less than 100 characters")
    @Schema(description = "Name of the workout session", example = "Morning Cardio", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Workout date must be in the past or present")
    @Schema(description = "The date the workout was performed", example = "2025-11-12", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be a positive number")
    @Schema(description = "Duration of the workout in minutes", example = "45", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration;

    @NotNull(message = "Calories cannot be null")
    @Positive(message = "Calories burned must be a positive number")
    @Schema(description = "Estimated calories burned during the workout", example = "350", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer calories;

    @Schema(description = "Optional notes about the workout session", example = "Felt great, good pace.")
    private String notes;

    @Schema(description = "Type of the workout", example = "CARDIO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Workout type cannot be null")
    private WorkoutType type;
}
