package com.example.fitnesstracker.controller.impl;

import com.example.fitnesstracker.controller.WorkoutControllerDocs;
import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.WorkoutFilterDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController implements WorkoutControllerDocs {

    private final WorkoutService workoutService;

    @Override
    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(@Valid @RequestBody CreateWorkoutRequestDto requestDto) {
        WorkoutResponseDto createdWorkout = workoutService.createWorkout(requestDto);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(@PathVariable Long id) {
        WorkoutResponseDto workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @Override
    @GetMapping
    public ResponseEntity<WorkoutPageResponseDto> getWorkouts(@ModelAttribute WorkoutFilterDto filter,
                                                              @PageableDefault(size = 10, sort = "date") Pageable pageable
    ) {
        return ResponseEntity.ok(workoutService.getAllWorkouts(filter, pageable));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(@PathVariable Long id, @Valid @RequestBody UpdateWorkoutRequestDto requestDto) {
        WorkoutResponseDto updatedWorkout = workoutService.updateWorkout(id, requestDto);
        return ResponseEntity.ok(updatedWorkout);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

}
