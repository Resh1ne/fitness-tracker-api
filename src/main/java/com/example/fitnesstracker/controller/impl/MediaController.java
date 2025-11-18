package com.example.fitnesstracker.controller.impl;

import com.example.fitnesstracker.controller.MediaControllerDocs;
import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController implements MediaControllerDocs {

    private final MediaService mediaService;

    @Override
    @PostMapping
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file,
                                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Long mediaId = mediaService.uploadPhoto(file, userDetails.getUsername());
        URI location = URI.create("/api/v1/media/" + mediaId);
        return ResponseEntity.created(location).body("Media uploaded successfully with ID: " + mediaId);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getMedia(@PathVariable("id") Long mediaId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        ProgressPhoto photoMetadata = mediaService.getPhotoMetadata(mediaId, userDetails.getUsername());
        byte[] photoData = mediaService.getPhotoData(mediaId, userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photoMetadata.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(photoMetadata.getContentType()))
                .body(photoData);
    }
}