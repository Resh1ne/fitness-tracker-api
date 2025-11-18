package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Media Management", description = "APIs for uploading and retrieving user media (e.g., progress photos)")
public interface MediaControllerDocs {
    @Operation(summary = "Upload a media file (progress photo)",
            description = "Uploads a new photo for the authenticated user. The file is stored in an S3-compatible object storage (MinIO).")
    @RequestBody(description = "The media file to upload.", required = true,
            content = @Content(mediaType = "multipart/form-data",
                    schema = @Schema(type = "object", requiredProperties = "file"),
                    encoding = @Encoding(name = "file", contentType = "image/jpeg, image/png, image/gif")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media file uploaded successfully. The ID of the new resource is in the Location header.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Media uploaded successfully with ID: 1"))),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., no file part in the request or file is empty)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content)
    })
    ResponseEntity<String> uploadMedia(@Parameter(description = "The image file to be uploaded", required = true)
                                       @RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetails userDetails) throws IOException;


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
    ResponseEntity<byte[]> getMedia(@Parameter(description = "ID of the media file to be retrieved", required = true, example = "1")
                                    @PathVariable("id") Long mediaId,
                                    @AuthenticationPrincipal UserDetails userDetails);
}
