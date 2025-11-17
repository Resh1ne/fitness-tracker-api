package com.example.fitnesstracker;

import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.ProgressPhotoRepository;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.service.impl.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.example.fitnesstracker.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgressPhotoRepository photoRepository;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private User ownerUser;
    private ProgressPhoto progressPhoto;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ownerUser = TestDataFactory.createOwnerUser();
        progressPhoto = TestDataFactory.createProgressPhoto(ownerUser);
        mockFile = TestDataFactory.createMockMultipartFile();
    }

    @Test
    @DisplayName("uploadPhoto should save photo and return its ID when user exists")
    void uploadPhoto_whenUserExists_shouldSavePhotoAndReturnId() throws IOException {
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(ownerUser));
        ArgumentCaptor<ProgressPhoto> photoArgumentCaptor = ArgumentCaptor.forClass(ProgressPhoto.class);
        when(photoRepository.save(photoArgumentCaptor.capture())).thenReturn(progressPhoto);

        Long savedPhotoId = mediaService.uploadPhoto(mockFile, OWNER_EMAIL);

        assertThat(savedPhotoId).isEqualTo(PHOTO_ID);
        ProgressPhoto capturedPhoto = photoArgumentCaptor.getValue();
        assertThat(capturedPhoto.getFilename()).isEqualTo(PHOTO_FILENAME);
        assertThat(capturedPhoto.getContentType()).isEqualTo(PHOTO_CONTENT_TYPE);
        assertThat(capturedPhoto.getData()).isEqualTo(PHOTO_DATA);
        assertThat(capturedPhoto.getUser()).isEqualTo(ownerUser);
        verify(userRepository).findByEmail(OWNER_EMAIL);
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
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(progressPhoto));

        ProgressPhoto foundPhoto = mediaService.getPhoto(PHOTO_ID, OWNER_EMAIL);

        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getId()).isEqualTo(PHOTO_ID);
        assertThat(foundPhoto.getUser().getEmail()).isEqualTo(OWNER_EMAIL);
        verify(photoRepository).findById(PHOTO_ID);
    }

    @Test
    @DisplayName("getPhoto should throw ResourceNotFoundException when photo does not exist")
    void getPhoto_whenPhotoDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        when(photoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.getPhoto(nonExistentId, OWNER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("getPhoto should throw AccessDeniedException when requested by non-owner")
    void getPhoto_whenRequestedByNonOwner_shouldThrowAccessDeniedException() {
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(progressPhoto));

        assertThatThrownBy(() -> mediaService.getPhoto(PHOTO_ID, OTHER_USER_EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to view this photo");
    }
}