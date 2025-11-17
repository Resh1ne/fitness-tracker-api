package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.ProgressPhotoRepository;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final UserRepository userRepository;
    private final ProgressPhotoRepository photoRepository;

    @Override
    @Transactional
    public Long uploadPhoto(MultipartFile file, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        ProgressPhoto photo = ProgressPhoto.builder()
                .filename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .data(file.getBytes())
                .uploadTime(LocalDateTime.now())
                .user(user)
                .build();

        ProgressPhoto savedPhoto = photoRepository.save(photo);
        return savedPhoto.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressPhoto getPhoto(Long photoId, String userEmail) {
        ProgressPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        if (!photo.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to view this photo");
        }

        return photo;
    }
}
