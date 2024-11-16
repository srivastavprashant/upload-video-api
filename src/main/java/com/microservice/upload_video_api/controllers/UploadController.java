package com.microservice.upload_video_api.controllers;

import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.models.ResponseMessage;
import com.microservice.upload_video_api.services.UploadService;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload-video")
@RequiredArgsConstructor
public class UploadController {

    final UploadService uploadService;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName = "your-s3-bucket";

    @PostMapping(path = "/upload")
    public ResponseEntity<ResponseMessage> upload(@RequestParam("videoFile") MultipartFile videoFile, @RequestPart("video") Video video) {
        // Implement the logic to save the video and return the response field with the video URL and thumbnail URL
        var videoEntityData = uploadService.saveVideo(video, videoFile);
        ResponseMessage response = ResponseMessage.builder()
                .message("Successful")
                .exceptionMessage("")
                .exceptionOccurred("N")
                .data(videoEntityData)
                .internalStatusCode("100")
                .build();
        response.setData(videoEntityData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/initiate-upload")
    public Map<String, String> initiateMultipartUpload(@RequestParam String fileName) {
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
        var uploadId = response.uploadId();

        Map<String, String> result = new HashMap<>();
        result.put("uploadId", uploadId);
        return result;
    }

    // Step 2: Generate Pre-signed URLs for Each Part
    public Map<String, String> generatePresignedUrl(String fileName, String uploadId, int partNumber) {
        try {
            // Create the upload part request
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength(5 * 1024 * 1024L) // Set content length as needed (5 MB for example)
                    .build();

            // Create the presigned request for uploading a part
            UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest) // Provide the UploadPartRequest directly
                    .signatureDuration(Duration.ofMinutes(15)) // Set the presigned URL expiration time
                    .build();

            // Generate the presigned URL
            var preSignedResponse = s3Presigner.presignUploadPart(presignRequest);
            // Prepare the response map
            Map<String, String> response = new HashMap<>();
            response.put("url", preSignedResponse.url().toString());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    // Step 3: Complete Multipart Upload
    @PostMapping("/complete-upload")
    public Map<String, String> completeMultipartUpload(
            @RequestParam String fileName,
            @RequestParam String uploadId,
            @RequestBody List<CompletedPart> completedParts) {

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();

        CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        Map<String, String> result = new HashMap<>();
        result.put("ETag", response.eTag());
        return result;
    }
}

