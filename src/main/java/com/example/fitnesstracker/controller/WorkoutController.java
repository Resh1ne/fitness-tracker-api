package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.*;
import com.example.fitnesstracker.entity.enums.WorkoutType;
import com.example.fitnesstracker.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Workout Management", description = "APIs for creating, retrieving, updating, and deleting workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Operation(summary = "Create a new workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(@Valid @RequestBody CreateWorkoutRequestDto requestDto) {
        WorkoutResponseDto createdWorkout = workoutService.createWorkout(requestDto);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a specific workout by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout found and returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Workout with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> getWorkoutById(
            @Parameter(description = "ID of the workout to be retrieved", required = true, example = "1")
            @PathVariable Long id
    ) {
        WorkoutResponseDto workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @Operation(summary = "Get all workouts with filters, sorting, and pagination",
            description = "Retrieves a paginated list of workouts. Allows filtering by type, date range, and duration. Supports sorting by date and calories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of workouts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutPageResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content)
    })
    @Parameters({
            @Parameter(name = "type", description = "Filter by workout type", example = "CARDIO"),
            @Parameter(name = "dateFrom", description = "Filter workouts from this date (inclusive). Format: YYYY-MM-DD", example = "2025-01-01"),
            @Parameter(name = "dateTo", description = "Filter workouts up to this date (inclusive). Format: YYYY-MM-DD", example = "2025-11-30"),
            @Parameter(name = "durationFrom", description = "Minimum workout duration in minutes", example = "30"),
            @Parameter(name = "durationTo", description = "Maximum workout duration in minutes", example = "90"),
            @Parameter(name = "sortBy", description = "Property to sort by. Allowed values: 'date', 'calories'", example = "calories"),
            @Parameter(name = "sortDir", description = "Sort direction. Allowed values: 'ASC', 'DESC'", example = "DESC"),
            @Parameter(name = "page", description = "Page number (0..N)", example = "0"),
            @Parameter(name = "size", description = "Number of elements per page", example = "10")
    })
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

    @Operation(summary = "Update an existing workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Workout with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(
            @Parameter(description = "ID of the workout to be updated", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutRequestDto requestDto
    ) {
        WorkoutResponseDto updatedWorkout = workoutService.updateWorkout(id, requestDto);
        return ResponseEntity.ok(updatedWorkout);
    }

    @Operation(summary = "Delete a workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Workout deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Workout with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> deleteWorkout(
            @Parameter(description = "ID of the workout to be deleted", required = true, example = "1")
            @PathVariable Long id
    ) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

}
