package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    WorkoutResponseDto toDto(Workout workout);

    Workout toEntity(CreateWorkoutRequestDto dto);

    void updateWorkoutFromDto(UpdateWorkoutRequestDto dto, @MappingTarget Workout workout);
}
