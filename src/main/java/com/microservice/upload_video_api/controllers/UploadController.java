package com.microservice.upload_video_api.controllers;

import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.dto.records.UploadInitiateResponse;
import com.microservice.upload_video_api.entities.S3UploadedVideoDescriptionData;
import com.microservice.upload_video_api.models.ResponseMessage;
import com.microservice.upload_video_api.repositories.S3UploadedVideoDescriptionDataRepository;
import com.microservice.upload_video_api.services.UploadService;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@RestController
@RequestMapping("/upload-video")
@RequiredArgsConstructor
public class UploadController {

    final UploadService uploadService;

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
    public ResponseEntity<ResponseMessage> initiateMultipartUpload(@RequestParam String fileName) {
        var initiateResponse = uploadService.initiateMultipartUpload(fileName);
        ResponseMessage response = ResponseMessage.builder()
                .message("Successful")
                .exceptionMessage("")
                .exceptionOccurred("N")
                .data(initiateResponse)
                .internalStatusCode("100")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Step 2: Generate Pre-signed URLs for Each Part
    @PostMapping("/generate-pre-signed-url")
    public ResponseEntity<ResponseMessage> generatePreSignedUrl(String fileName, String uploadId, int partNumber) {
        var generatedUrlObject =  uploadService.generatePreSignedUrl(fileName, uploadId, partNumber);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Step 3: Complete Multipart Upload
    @PostMapping("/complete-upload")
    public ResponseEntity<ResponseMessage> completeMultipartUpload(
            @RequestParam String fileName,
            @RequestParam String uploadId,
            @RequestBody List<CompletedPart> completedParts) {
        var generatedUrlObject = uploadService.completeMultipartUpload(fileName, uploadId, completedParts);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

