package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.ProgressPhotoRepository;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final UserRepository userRepository;
    private final ProgressPhotoRepository photoRepository;
    private final S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public Long uploadPhoto(MultipartFile file, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        String objectKey = generateObjectKey(file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        ProgressPhoto photo = ProgressPhoto.builder()
                .filename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .objectKey(objectKey)
                .uploadTime(LocalDateTime.now())
                .user(user)
                .build();

        ProgressPhoto savedPhoto = photoRepository.save(photo);
        return savedPhoto.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getPhotoData(Long photoId, String userEmail) {
        ProgressPhoto photo = getPhotoMetadata(photoId, userEmail);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(photo.getObjectKey())
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return responseBytes.asByteArray();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressPhoto getPhotoMetadata(Long photoId, String userEmail) {
        ProgressPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        if (!photo.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to view this photo");
        }
        return photo;
    }

    private String generateObjectKey(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }
}