package com.example.fitnesstracker.service;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.enums.WorkoutType;

import java.time.LocalDate;

public interface WorkoutService {
    WorkoutResponseDto createWorkout(CreateWorkoutRequestDto requestDto);


    WorkoutResponseDto getWorkoutById(Long id);

    WorkoutResponseDto updateWorkout(Long id, UpdateWorkoutRequestDto requestDto);

    void deleteWorkout(Long id);

    WorkoutPageResponseDto getAllWorkouts(WorkoutType type, LocalDate dateFrom, LocalDate dateTo, Integer durationFrom,
                                          Integer durationTo, String sortBy, String sortDir, int page, int size);
}
