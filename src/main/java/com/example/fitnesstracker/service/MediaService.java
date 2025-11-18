package com.example.fitnesstracker.service;

import com.example.fitnesstracker.entity.ProgressPhoto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaService {
    Long uploadPhoto(MultipartFile file, String userEmail) throws IOException;

    byte[] getPhotoData(Long photoId, String userEmail);

    ProgressPhoto getPhotoMetadata(Long photoId, String userEmail);
}
