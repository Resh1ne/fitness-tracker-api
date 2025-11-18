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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Optional;

import static com.example.fitnesstracker.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgressPhotoRepository photoRepository;

    @Mock
    private S3Client s3Client;

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
        ReflectionTestUtils.setField(mediaService, "bucketName", "progress-photos");
    }

    @Test
    @DisplayName("uploadPhoto should upload to S3, save metadata and return ID")
    void uploadPhoto_shouldUploadToS3AndSaveMetadata() throws IOException {
        when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(ownerUser));
        ArgumentCaptor<ProgressPhoto> photoArgumentCaptor = ArgumentCaptor.forClass(ProgressPhoto.class);
        when(photoRepository.save(photoArgumentCaptor.capture())).thenReturn(progressPhoto);

        Long savedPhotoId = mediaService.uploadPhoto(mockFile, OWNER_EMAIL);

        assertThat(savedPhotoId).isEqualTo(PHOTO_ID);
        ProgressPhoto capturedPhoto = photoArgumentCaptor.getValue();
        assertThat(capturedPhoto.getFilename()).isEqualTo(PHOTO_FILENAME);
        assertThat(capturedPhoto.getObjectKey()).isNotNull();
        assertThat(capturedPhoto.getUser()).isEqualTo(ownerUser);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(photoRepository).save(any(ProgressPhoto.class));
    }

    @Test
    @DisplayName("getPhotoMetadata should return metadata when requested by owner")
    void getPhotoMetadata_whenRequestedByOwner_shouldReturnMetadata() {
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(progressPhoto));

        ProgressPhoto foundPhoto = mediaService.getPhotoMetadata(PHOTO_ID, OWNER_EMAIL);

        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getId()).isEqualTo(PHOTO_ID);
        assertThat(foundPhoto.getObjectKey()).isEqualTo(PHOTO_OBJECT_KEY);
        verify(photoRepository).findById(PHOTO_ID);
    }

    @Test
    @DisplayName("getPhotoData should download from S3 and return byte array for owner")
    void getPhotoData_whenRequestedByOwner_shouldDownloadAndReturnBytes() {
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(progressPhoto));
        GetObjectResponse s3Response = GetObjectResponse.builder().build();
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(s3Response, PHOTO_DATA);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        byte[] data = mediaService.getPhotoData(PHOTO_ID, OWNER_EMAIL);

        assertThat(data).isEqualTo(PHOTO_DATA);
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("getPhotoMetadata should throw ResourceNotFoundException when photo does not exist")
    void getPhotoMetadata_whenPhotoDoesNotExist_shouldThrowException() {
        long nonExistentId = 99L;
        when(photoRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.getPhotoMetadata(nonExistentId, OWNER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Photo not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("getPhotoMetadata should throw AccessDeniedException when requested by non-owner")
    void getPhotoMetadata_whenRequestedByNonOwner_shouldThrowException() {
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(progressPhoto));

        assertThatThrownBy(() -> mediaService.getPhotoMetadata(PHOTO_ID, OTHER_USER_EMAIL))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to view this photo");
    }
}