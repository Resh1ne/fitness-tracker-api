package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;
import com.example.fitnesstracker.entity.enums.WorkoutType;
import com.example.fitnesstracker.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(@Valid @RequestBody CreateWorkoutRequestDto requestDto) {
        WorkoutResponseDto createdWorkout = workoutService.createWorkout(requestDto);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(@PathVariable Long id) {
        WorkoutResponseDto workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @GetMapping
    public ResponseEntity<WorkoutPageResponseDto> getWorkouts(
            @RequestParam(required = false) WorkoutType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Integer durationFrom,
            @RequestParam(required = false) Integer durationTo,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(workoutService.getAllWorkouts(type, dateFrom, dateTo, durationFrom, durationTo,
                sortBy, sortDir, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(@PathVariable Long id, @Valid @RequestBody UpdateWorkoutRequestDto requestDto) {
        WorkoutResponseDto updatedWorkout = workoutService.updateWorkout(id, requestDto);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

}
