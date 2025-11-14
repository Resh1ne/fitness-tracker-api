package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.entity.enums.WorkoutType;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutMapper;
import com.example.fitnesstracker.repository.WorkoutRepository;
import com.example.fitnesstracker.repository.specification.WorkoutSpecification;
import com.example.fitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    public WorkoutPageResponseDto getAllWorkouts(WorkoutType type, LocalDate dateFrom, LocalDate dateTo,
                                                 Integer durationFrom, Integer durationTo, String sortBy, String sortDir,
                                                 int page, int size) {

        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "date");

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Workout> spec = WorkoutSpecification.hasType(type)
                .and(WorkoutSpecification.dateFrom(dateFrom))
                .and(WorkoutSpecification.dateTo(dateTo))
                .and(WorkoutSpecification.durationFrom(durationFrom))
                .and(WorkoutSpecification.durationTo(durationTo));


        Page<Workout> result = workoutRepository.findAll(spec, pageable);

        WorkoutPageResponseDto response = new WorkoutPageResponseDto();
        response.setContent(result.getContent().stream()
                .map(workoutMapper::toDto)
                .toList());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setPage(page);
        response.setSize(size);

        return response;
    }
}
