package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.WorkoutFilterDto;
import com.example.fitnesstracker.dto.response.ErrorResponse;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Workout Management", description = "APIs for creating, retrieving, updating, and deleting workouts")
public interface WorkoutControllerDocs {
    @Operation(summary = "Create a new workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workout created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<WorkoutResponseDto> createWorkout(@RequestBody CreateWorkoutRequestDto requestDto);


    @Operation(summary = "Get a specific workout by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workout found and returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Workout with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutResponseDto> getWorkoutById(@Parameter(description = "ID of the workout to be retrieved", required = true, example = "1")
                                                      @PathVariable Long id);


    @Operation(summary = "Get all workouts with filters, sorting, and pagination",
            description = "Retrieves a paginated list of workouts. Allows filtering by type, date range, and duration. Supports sorting by date and calories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of workouts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkoutPageResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content)
    })
    ResponseEntity<WorkoutPageResponseDto> getWorkouts(@ParameterObject @ModelAttribute WorkoutFilterDto filter,
                                                       @ParameterObject Pageable pageable);


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
    ResponseEntity<WorkoutResponseDto> updateWorkout(@Parameter(description = "ID of the workout to be updated", required = true, example = "1")
                                                     @PathVariable Long id,
                                                     @RequestBody UpdateWorkoutRequestDto requestDto);


    @Operation(summary = "Delete a workout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Workout deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Workout with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteWorkout(@Parameter(description = "ID of the workout to be deleted", required = true, example = "1")
                                       @PathVariable Long id);
}
