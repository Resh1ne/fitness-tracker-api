package com.example.fitnesstracker.service;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;

import java.util.List;

public interface WorkoutService {
    WorkoutResponseDto createWorkout(CreateWorkoutRequestDto requestDto);

    List<WorkoutResponseDto> getAllWorkouts();

    WorkoutResponseDto getWorkoutById(Long id);

    WorkoutResponseDto updateWorkout(Long id, UpdateWorkoutRequestDto requestDto);

    void deleteWorkout(Long id);
}
