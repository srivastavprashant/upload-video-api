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
}

