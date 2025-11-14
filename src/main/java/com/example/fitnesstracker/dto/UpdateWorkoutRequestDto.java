package com.example.fitnesstracker.dto;

import com.example.fitnesstracker.entity.enums.WorkoutType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateWorkoutRequestDto {
    @NotBlank(message = "Workout name cannot be empty")
    @Size(max = 100, message = "Workout name must be less than 100 characters")
    private String name;
    private LocalDate date;
    private Integer duration;
    private Integer calories;
    private String notes;
    private WorkoutType type;
}

