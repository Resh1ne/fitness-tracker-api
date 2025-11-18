package com.example.fitnesstracker.service;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.WorkoutFilterDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import org.springframework.data.domain.Pageable;

public interface WorkoutService {
    WorkoutResponseDto createWorkout(CreateWorkoutRequestDto requestDto);


    WorkoutResponseDto getWorkoutById(Long id);

    WorkoutResponseDto updateWorkout(Long id, UpdateWorkoutRequestDto requestDto);

    void deleteWorkout(Long id);

    WorkoutPageResponseDto getAllWorkouts(WorkoutFilterDto filter, Pageable pageable);
}
