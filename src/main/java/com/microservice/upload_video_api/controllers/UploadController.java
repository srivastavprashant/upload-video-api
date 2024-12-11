package com.microservice.upload_video_api.controllers;

import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.ResponseMessage;
import com.microservice.upload_video_api.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "Video Upload", description = "Video Upload API endpoints")
public class  UploadController {

    final UploadService uploadService;
    @Operation(summary = "Upload a video",
            description = "Upload a video file to the system")
    @PostMapping(path = "/upload-full")
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
    public ResponseEntity<ResponseMessage> initiateMultipartUpload(@RequestBody InitiateUploadRequest initiateUploadRequest) {
        var initiateResponse = uploadService.initiateMultipartUpload(initiateUploadRequest);
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
    public ResponseEntity<ResponseMessage> generatePreSignedUrl(String fileName, String uploadId, int partNumber, Long contentLength) {
        var generatedUrlObject =  uploadService.generatePreSignedUrl(fileName, uploadId, partNumber, contentLength);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Step 3: Complete Multipart Upload
    @PostMapping("/complete")
    public ResponseEntity<ResponseMessage> completeMultipartUpload(
            @RequestParam String fileName,
            @RequestParam String uploadId,
            @RequestBody List<ETagList> eTagList) {
        var generatedUrlObject = uploadService.completeMultipartUpload(fileName, uploadId, eTagList);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

