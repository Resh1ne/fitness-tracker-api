package com.example.fitnesstracker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateWorkoutRequestDto {
    @NotBlank(message = "Workout name cannot be empty")
    @Size(max = 100, message = "Workout name must be less than 100 characters")
    private String name;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Workout date must be in the past or present")
    private LocalDate date;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be a positive number")
    private Integer duration;

    @NotNull(message = "Calories cannot be null")
    @Positive(message = "Calories burned must be a positive number")
    private Integer calories;
    private String notes;
}
