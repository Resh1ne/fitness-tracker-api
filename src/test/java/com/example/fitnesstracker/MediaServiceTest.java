package com.example.fitnesstracker;

import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.ProgressPhotoRepository;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.service.impl.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgressPhotoRepository photoRepository;

    @InjectMocks
    private MediaService mediaService;

    private User ownerUser;
    private ProgressPhoto progressPhoto;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder().id(1L).email("owner@example.com").build();
        User.builder().id(2L).email("other@example.com").build();

        progressPhoto = ProgressPhoto.builder()
                .id(10L)
                .filename("test-photo.jpg")
                .contentType("image/jpeg")
                .data(new byte[]{1, 2, 3})
                .user(ownerUser)
                .build();

        mockFile = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );
    }

    @Test
    @DisplayName("uploadPhoto should save photo and return its ID when user exists")
    void uploadPhoto_whenUserExists_shouldSavePhotoAndReturnId() throws IOException {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(ownerUser));
        ArgumentCaptor<ProgressPhoto> photoArgumentCaptor = ArgumentCaptor.forClass(ProgressPhoto.class);
        when(photoRepository.save(photoArgumentCaptor.capture())).thenReturn(progressPhoto);

        Long savedPhotoId = mediaService.uploadPhoto(mockFile, "owner@example.com");

        assertThat(savedPhotoId).isEqualTo(10L);
        ProgressPhoto capturedPhoto = photoArgumentCaptor.getValue();
        assertThat(capturedPhoto.getFilename()).isEqualTo("test-photo.jpg");
        assertThat(capturedPhoto.getContentType()).isEqualTo("image/jpeg");
        assertThat(capturedPhoto.getData()).isEqualTo(new byte[]{1, 2, 3});
        assertThat(capturedPhoto.getUser()).isEqualTo(ownerUser);
        verify(userRepository).findByEmail("owner@example.com");
        verify(photoRepository).save(any(ProgressPhoto.class));
    }

    @Test
    @DisplayName("uploadPhoto should throw ResourceNotFoundException when user does not exist")
    void uploadPhoto_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.uploadPhoto(mockFile, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with email: nonexistent@example.com");
        verify(photoRepository, never()).save(any());
    }

    @Test
    @DisplayName("getPhoto should return photo when requested by owner")
    void getPhoto_whenRequestedByOwner_shouldReturnPhoto() {
        when(photoRepository.findById(10L)).thenReturn(Optional.of(progressPhoto));

        ProgressPhoto foundPhoto = mediaService.getPhoto(10L, "owner@example.com");

        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getId()).isEqualTo(10L);
        assertThat(foundPhoto.getUser().getEmail()).isEqualTo("owner@example.com");
        verify(photoRepository).findById(10L);
    }

    @Test
    @DisplayName("getPhoto should throw ResourceNotFoundException when photo does not exist")
    void getPhoto_whenPhotoDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(photoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.getPhoto(nonExistentId, "owner@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("getPhoto should throw AccessDeniedException when requested by non-owner")
    void getPhoto_whenRequestedByNonOwner_shouldThrowAccessDeniedException() {
        when(photoRepository.findById(10L)).thenReturn(Optional.of(progressPhoto));

        assertThatThrownBy(() -> mediaService.getPhoto(10L, "other@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to view this photo");
    }
}