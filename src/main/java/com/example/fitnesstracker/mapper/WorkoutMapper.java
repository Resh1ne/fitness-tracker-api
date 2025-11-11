package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    WorkoutResponseDto toDto(Workout workout);

    Workout toEntity(CreateWorkoutRequestDto dto);

    void updateWorkoutFromDto(UpdateWorkoutRequestDto dto, @MappingTarget Workout workout);
}
