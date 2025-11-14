package com.example.fitnesstracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkoutPageResponseDto {
    private List<WorkoutResponseDto> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}