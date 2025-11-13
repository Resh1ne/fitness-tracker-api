package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutMapper;
import com.example.fitnesstracker.repository.WorkoutRepository;
import com.example.fitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;


    @Override
    @Transactional
    public WorkoutResponseDto createWorkout(CreateWorkoutRequestDto requestDto) {
        Workout workout = workoutMapper.toEntity(requestDto);
        Workout createWorkout = workoutRepository.save(workout);
        return workoutMapper.toDto(createWorkout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutResponseDto> getAllWorkouts() {
        return workoutRepository.findAll().stream()
                .map(workoutMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutResponseDto getWorkoutById(Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        return workoutMapper.toDto(workout);
    }

    @Override
    @Transactional
    public WorkoutResponseDto updateWorkout(Long id, UpdateWorkoutRequestDto requestDto) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        workoutMapper.updateWorkoutFromDto(requestDto, workout);
        Workout updatedWorkout = workoutRepository.save(workout);
        return workoutMapper.toDto(updatedWorkout);
    }

    @Override
    @Transactional
    public void deleteWorkout(Long id) {
        if (!workoutRepository.existsById(id)) {
            throw new ResourceNotFoundException("Workout not found with id: " + id);
        }
        workoutRepository.deleteById(id);

    }
}
