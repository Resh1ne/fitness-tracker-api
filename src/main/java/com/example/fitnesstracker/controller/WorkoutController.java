package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.WorkoutResponseDto;
import com.example.fitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(@RequestBody CreateWorkoutRequestDto requestDto) {
        WorkoutResponseDto createdWorkout = workoutService.createWorkout(requestDto);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(@PathVariable Long id) {
        WorkoutResponseDto workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponseDto>> getWorkouts() {
        List<WorkoutResponseDto> workouts = workoutService.getAllWorkouts();
        return ResponseEntity.ok(workouts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(@PathVariable Long id, @RequestBody UpdateWorkoutRequestDto requestDto) {
        WorkoutResponseDto updatedWorkout = workoutService.updateWorkout(id, requestDto);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

}
