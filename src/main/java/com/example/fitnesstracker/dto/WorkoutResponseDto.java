package com.example.fitnesstracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkoutResponseDto {
    private Long id;
    private String name;
    private LocalDate date;
    private Integer duration;
    private Integer calories;
    private String notes;
}