package com.example.fitnesstracker;

import com.example.fitnesstracker.dto.request.*;
import com.example.fitnesstracker.dto.response.WorkoutPageResponseDto;
import com.example.fitnesstracker.dto.response.WorkoutResponseDto;
import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.entity.enums.Role;
import com.example.fitnesstracker.entity.enums.WorkoutType;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class TestDataFactory {

    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String OWNER_EMAIL = "owner@example.com";
    public static final String OTHER_USER_EMAIL = "other@example.com";
    public static final String PHOTO_FILENAME = "test-photo.jpg";
    public static final String PHOTO_CONTENT_TYPE = "image/jpeg";
    public static final byte[] PHOTO_DATA = new byte[]{1, 2, 3};
    public static final Long PHOTO_ID = 10L;

    public static final Long WORKOUT_ID = 1L;
    public static final String WORKOUT_NAME = "Morning Run";
    public static final WorkoutType WORKOUT_TYPE_CARDIO = WorkoutType.CARDIO;
    public static final String UPDATE_WORKOUT_NAME = "Intense HIIT";
    public static final WorkoutType WORKOUT_TYPE_HIIT = WorkoutType.HIIT;

    public static User createUser() {
        return User.builder().id(1L).email(TEST_EMAIL).password(ENCODED_PASSWORD).role(Role.USER).build();
    }

    public static User createOwnerUser() {
        return User.builder().id(1L).email(OWNER_EMAIL).build();
    }

    public static Workout createWorkout() {
        return Workout.builder()
                .id(WORKOUT_ID)
                .name(WORKOUT_NAME)
                .date(LocalDate.now())
                .duration(30)
                .calories(250)
                .type(WORKOUT_TYPE_CARDIO)
                .build();
    }

    public static CreateWorkoutRequestDto createWorkoutRequestDto() {
        CreateWorkoutRequestDto dto = new CreateWorkoutRequestDto();
        dto.setName("Evening Yoga");
        dto.setDate(LocalDate.now().minusDays(1));
        dto.setDuration(60);
        dto.setCalories(150);
        dto.setType(WorkoutType.YOGA);
        return dto;
    }

    public static UpdateWorkoutRequestDto createUpdateWorkoutRequestDto() {
        UpdateWorkoutRequestDto dto = new UpdateWorkoutRequestDto();
        dto.setName(UPDATE_WORKOUT_NAME);
        dto.setType(WORKOUT_TYPE_HIIT);
        dto.setDate(LocalDate.now());
        dto.setDuration(25);
        dto.setCalories(400);
        return dto;
    }

    public static WorkoutResponseDto createWorkoutResponseDto(Workout workout) {
        WorkoutResponseDto dto = new WorkoutResponseDto();
        dto.setId(workout.getId());
        dto.setName(workout.getName());
        dto.setDate(workout.getDate());
        dto.setDuration(workout.getDuration());
        dto.setCalories(workout.getCalories());
        dto.setType(workout.getType());
        return dto;
    }

    public static WorkoutFilterDto createWorkoutFilterDto(WorkoutType type) {
        WorkoutFilterDto filter = new WorkoutFilterDto();
        filter.setType(type);
        return filter;
    }

    public static WorkoutPageResponseDto createWorkoutPageResponseDto(List<WorkoutResponseDto> content, Pageable pageable, long totalElements) {
        WorkoutPageResponseDto response = new WorkoutPageResponseDto();
        response.setContent(content);
        response.setPage(pageable.getPageNumber());
        response.setSize(pageable.getPageSize());
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / pageable.getPageSize()));
        return response;
    }

    public static ProgressPhoto createProgressPhoto(User owner) {
        return ProgressPhoto.builder().id(PHOTO_ID).filename(PHOTO_FILENAME).contentType(PHOTO_CONTENT_TYPE).data(PHOTO_DATA).user(owner).build();
    }

    public static MultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file", PHOTO_FILENAME, PHOTO_CONTENT_TYPE, PHOTO_DATA);
    }

    public static RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder().email(TEST_EMAIL).password(TEST_PASSWORD).build();
    }

    public static LoginRequest createLoginRequest() {
        return LoginRequest.builder().email(TEST_EMAIL).password(TEST_PASSWORD).build();
    }
}