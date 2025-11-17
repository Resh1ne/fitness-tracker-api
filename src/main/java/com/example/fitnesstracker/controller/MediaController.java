package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.response.ErrorResponse;
import com.example.fitnesstracker.entity.ProgressPhoto;
import com.example.fitnesstracker.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media Management", description = "APIs for uploading and retrieving user media (e.g., progress photos)")
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "Upload a media file (progress photo)",
            description = "Uploads a new photo for the authenticated user. The file is stored in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media file uploaded successfully. The ID of the new resource is in the Location header."),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> uploadMedia(
            @Parameter(description = "The image file to be uploaded", required = true)
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        Long mediaId = mediaService.uploadPhoto(file, userDetails.getUsername());
        URI location = URI.create("/api/v1/media/" + mediaId);
        return ResponseEntity.created(location).body("Media uploaded successfully with ID: " + mediaId);
    }

    @Operation(summary = "Download a media file by ID",
            description = "Retrieves a media file for the authenticated user. Users can only access their own files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media file retrieved successfully.",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to access this resource",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Media file with the specified ID not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getMedia(
            @Parameter(description = "ID of the media file to be retrieved", required = true, example = "1")
            @PathVariable("id") Long mediaId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProgressPhoto photo = mediaService.getPhoto(mediaId, userDetails.getUsername());

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(photo.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getData());
    }
}

