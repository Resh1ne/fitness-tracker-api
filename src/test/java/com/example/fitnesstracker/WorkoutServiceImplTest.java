package com.example.fitnesstracker;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.WorkoutFilterDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutMapper;
import com.example.fitnesstracker.repository.WorkoutRepository;
import com.example.fitnesstracker.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.example.fitnesstracker.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutMapper workoutMapper;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    private Workout workout;
    private WorkoutResponseDto workoutResponseDto;
    private CreateWorkoutRequestDto createWorkoutRequestDto;
    private UpdateWorkoutRequestDto updateWorkoutRequestDto;

    @BeforeEach
    void setUp() {
        workout = TestDataFactory.createWorkout();
        workoutResponseDto = TestDataFactory.createWorkoutResponseDto(workout);
        createWorkoutRequestDto = TestDataFactory.createWorkoutRequestDto();
        updateWorkoutRequestDto = TestDataFactory.createUpdateWorkoutRequestDto();
    }

    @Test
    @DisplayName("getWorkoutById should return Workout DTO when workout exists")
    void getWorkoutById_whenWorkoutExists_shouldReturnWorkoutDto() {
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));
        when(workoutMapper.toDto(workout)).thenReturn(workoutResponseDto);

        WorkoutResponseDto foundDto = workoutService.getWorkoutById(WORKOUT_ID);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(WORKOUT_ID);
        verify(workoutRepository).findById(WORKOUT_ID);
        verify(workoutMapper).toDto(workout);
    }

    @Test
    @DisplayName("getWorkoutById should throw ResourceNotFoundException when workout does not exist")
    void getWorkoutById_whenWorkoutDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(workoutRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.getWorkoutById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workout not found with id: " + nonExistentId);
        verify(workoutRepository).findById(nonExistentId);
        verify(workoutMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createWorkout should save and return new Workout DTO")
    void createWorkout_shouldSaveAndReturnNewWorkoutDto() {
        Workout newWorkout = new Workout();
        newWorkout.setName(createWorkoutRequestDto.getName());
        when(workoutMapper.toEntity(createWorkoutRequestDto)).thenReturn(newWorkout);
        when(workoutRepository.save(newWorkout)).thenReturn(workout);
        when(workoutMapper.toDto(workout)).thenReturn(workoutResponseDto);

        WorkoutResponseDto createdDto = workoutService.createWorkout(createWorkoutRequestDto);

        assertThat(createdDto).isNotNull();
        assertThat(createdDto.getId()).isEqualTo(WORKOUT_ID);
        verify(workoutRepository).save(newWorkout);
    }

    @Test
    @DisplayName("updateWorkout should update and return DTO when workout exists")
    void updateWorkout_whenWorkoutExists_shouldUpdateAndReturnDto() {
        when(workoutRepository.findById(WORKOUT_ID)).thenReturn(Optional.of(workout));
        doNothing().when(workoutMapper).updateWorkoutFromDto(eq(updateWorkoutRequestDto), any(Workout.class));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(workoutMapper.toDto(any(Workout.class))).thenReturn(workoutResponseDto);

        WorkoutResponseDto resultDto = workoutService.updateWorkout(WORKOUT_ID, updateWorkoutRequestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(WORKOUT_ID);
        verify(workoutRepository).findById(WORKOUT_ID);
        verify(workoutMapper).updateWorkoutFromDto(updateWorkoutRequestDto, workout);
        verify(workoutRepository).save(workout);
    }

    @Test
    @DisplayName("updateWorkout should throw ResourceNotFoundException when workout does not exist")
    void updateWorkout_whenWorkoutDoesNotExist_shouldThrowException() {
        long nonExistentId = 99L;
        when(workoutRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.updateWorkout(nonExistentId, updateWorkoutRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workout not found with id: " + nonExistentId);
        verify(workoutMapper, never()).updateWorkoutFromDto(any(), any());
        verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteWorkout should call deleteById when workout exists")
    void deleteWorkout_whenWorkoutExists_shouldCallDeleteById() {
        when(workoutRepository.existsById(WORKOUT_ID)).thenReturn(true);
        doNothing().when(workoutRepository).deleteById(WORKOUT_ID);

        workoutService.deleteWorkout(WORKOUT_ID);

        verify(workoutRepository, times(1)).deleteById(WORKOUT_ID);
    }

    @Test
    @DisplayName("deleteWorkout should throw ResourceNotFoundException when workout does not exist")
    void deleteWorkout_whenWorkoutDoesNotExist_shouldThrowException() {
        long nonExistentId = 99L;
        when(workoutRepository.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> workoutService.deleteWorkout(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(workoutRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getAllWorkouts should return a paginated response based on filter and pageable")
    void getAllWorkouts_shouldReturnPaginatedResponse() {
        WorkoutFilterDto filter = createWorkoutFilterDto(WORKOUT_TYPE_CARDIO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Workout> workoutPage = new PageImpl<>(List.of(workout), pageable, 1);
        WorkoutPageResponseDto expectedResponse = createWorkoutPageResponseDto(List.of(workoutResponseDto), pageable, 1);

        when(workoutRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(workoutPage);
        when(workoutMapper.toPageResponseDto(workoutPage)).thenReturn(expectedResponse);

        WorkoutPageResponseDto actualResult = workoutService.getAllWorkouts(filter, pageable);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPage()).isEqualTo(0);
        assertThat(actualResult.getTotalElements()).isEqualTo(1);
        assertThat(actualResult.getContent()).hasSize(1);
        assertThat(actualResult.getContent().getFirst().getName()).isEqualTo(WORKOUT_NAME);
        verify(workoutRepository).findAll(any(Specification.class), eq(pageable));
        verify(workoutMapper).toPageResponseDto(workoutPage);
    }
}