package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.WorkoutFilterDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutMapper;
import com.example.fitnesstracker.repository.WorkoutRepository;
import com.example.fitnesstracker.repository.specification.WorkoutSpecification;
import com.example.fitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(readOnly = true)
    public WorkoutPageResponseDto getAllWorkouts(WorkoutFilterDto filter, Pageable pageable) {
        Specification<Workout> spec = WorkoutSpecification.build(filter);
        Page<Workout> workoutPage = workoutRepository.findAll(spec, pageable);
        return workoutMapper.toPageResponseDto(workoutPage);
    }
}
