package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    WorkoutResponseDto toDto(Workout workout);

    Workout toEntity(CreateWorkoutRequestDto dto);

    void updateWorkoutFromDto(UpdateWorkoutRequestDto dto, @MappingTarget Workout workout);

    default WorkoutPageResponseDto toPageResponseDto(Page<Workout> workoutPage) {
        WorkoutPageResponseDto response = new WorkoutPageResponseDto();
        response.setContent(workoutPage.getContent().stream().map(this::toDto).toList());
        response.setTotalElements(workoutPage.getTotalElements());
        response.setTotalPages(workoutPage.getTotalPages());
        response.setPage(workoutPage.getNumber());
        response.setSize(workoutPage.getSize());
        return response;
    }
}
