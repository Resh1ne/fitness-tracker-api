package com.example.fitnesstracker;

import com.example.fitnesstracker.dto.request.CreateWorkoutRequestDto;
import com.example.fitnesstracker.dto.request.UpdateWorkoutRequestDto;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.entity.enums.WorkoutType;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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
    private WorkoutResponseDto workoutDto;
    private CreateWorkoutRequestDto createDto;
    private UpdateWorkoutRequestDto updateDto;

    @BeforeEach
    void setUp() {
        workout = new Workout();
        workout.setId(1L);
        workout.setName("Morning Run");
        workout.setDate(LocalDate.now());
        workout.setType(WorkoutType.CARDIO);

        workoutDto = new WorkoutResponseDto();
        workoutDto.setId(1L);
        workoutDto.setName("Morning Run");
        workoutDto.setType(WorkoutType.CARDIO);

        createDto = new CreateWorkoutRequestDto();
        createDto.setName("Evening Yoga");

        updateDto = new UpdateWorkoutRequestDto();
        updateDto.setName("Intense HIIT");
        updateDto.setType(WorkoutType.HIIT);
    }

    @Test
    @DisplayName("getWorkoutById should return Workout DTO when workout exists")
    void getWorkoutById_whenWorkoutExists_shouldReturnWorkoutDto() {
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        when(workoutMapper.toDto(workout)).thenReturn(workoutDto);

        WorkoutResponseDto foundDto = workoutService.getWorkoutById(1L);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1L);
        verify(workoutRepository).findById(1L);
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
        Workout workoutToSave = new Workout();
        workoutToSave.setName(createDto.getName());
        when(workoutMapper.toEntity(createDto)).thenReturn(workoutToSave);
        when(workoutRepository.save(workoutToSave)).thenReturn(workout);
        when(workoutMapper.toDto(workout)).thenReturn(workoutDto);

        WorkoutResponseDto createdDto = workoutService.createWorkout(createDto);

        assertThat(createdDto).isNotNull();
        assertThat(createdDto.getId()).isEqualTo(1L);
        verify(workoutRepository).save(workoutToSave);
    }

    @Test
    @DisplayName("updateWorkout should update and return DTO when workout exists")
    void updateWorkout_whenWorkoutExists_shouldUpdateAndReturnDto() {
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));
        doNothing().when(workoutMapper).updateWorkoutFromDto(eq(updateDto), any(Workout.class));
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(workoutMapper.toDto(any(Workout.class))).thenReturn(workoutDto);

        WorkoutResponseDto resultDto = workoutService.updateWorkout(1L, updateDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(1L);
        verify(workoutRepository).findById(1L);
        verify(workoutMapper).updateWorkoutFromDto(updateDto, workout);
        verify(workoutRepository).save(workout);
        verify(workoutMapper).toDto(workout);
    }

    @Test
    @DisplayName("updateWorkout should throw ResourceNotFoundException when workout does not exist")
    void updateWorkout_whenWorkoutDoesNotExist_shouldThrowException() {
        long nonExistentId = 99L;
        when(workoutRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.updateWorkout(nonExistentId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Workout not found with id: " + nonExistentId);
        verify(workoutMapper, never()).updateWorkoutFromDto(any(), any());
        verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteWorkout should call deleteById when workout exists")
    void deleteWorkout_whenWorkoutExists_shouldCallDeleteById() {
        long existingId = 1L;
        when(workoutRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(workoutRepository).deleteById(existingId);

        workoutService.deleteWorkout(existingId);

        verify(workoutRepository, times(1)).deleteById(existingId);
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
    @DisplayName("getAllWorkouts should return a paginated response of workouts")
    void getAllWorkouts_shouldReturnPaginatedResponse() {
        int page = 0;
        int size = 10;
        Page<Workout> workoutPage = new PageImpl<>(List.of(workout));
        when(workoutRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(workoutPage);
        when(workoutMapper.toDto(workout)).thenReturn(workoutDto);

        WorkoutPageResponseDto result = workoutService.getAllWorkouts(
                WorkoutType.CARDIO, null, null, null, null, "date", "ASC", page, size);

        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(page);
        assertThat(result.getSize()).isEqualTo(size);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getType()).isEqualTo(WorkoutType.CARDIO);
        verify(workoutRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(workoutMapper, times(1)).toDto(any(Workout.class));
    }
}
