package com.microservice.upload_video_api.controllers;

import com.microservice.upload_video_api.models.dto.CompleteVideoUpload.req.CompleteVideoUploadRequest;
import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
import com.microservice.upload_video_api.models.dto.PreSign.req.PreSignRequest;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.ResponseMessage;
import com.microservice.upload_video_api.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/upload-multi-part")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Video Upload Method - 2", description = "Video Upload via multi part")
public class MultiPartUploadController {

    final UploadService uploadService;
    // Step 1: Initiate Upload API - return uploadId
    @Operation(summary = "Initiate Upload",
            description = "Step 1 - Initiate Upload. It returns uploadId, use this upload id to generate S3 Url for each part of file")
    @PostMapping("/initiate-upload")
    public ResponseEntity<ResponseMessage> initiateMultipartUpload(@RequestBody InitiateUploadRequest initiateUploadRequest) {
        log.info(initiateUploadRequest);
        var initiateResponse = uploadService.initiateMultipartUpload(initiateUploadRequest);
        log.info(initiateResponse);
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
    @Operation(summary = "Generate Pre Sign URL or S3 url to upload parts to s3. Extract ETAG from response header after uploading",
            description = "Step 2 - Generate URL using Upload Id")
    @PostMapping("/generate-pre-signed-url")
    public ResponseEntity<ResponseMessage> generatePreSignedUrl(@RequestBody PreSignRequest preSignRequest) {
        log.info(preSignRequest);
        var generatedUrlObject = uploadService.generatePreSignedUrlMultiPartMethod(preSignRequest);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Step 3: Complete Multipart Upload
    @Operation(summary = "Send all ETags as list to complete the API. This will trigger S3 to merge all parts into 1 file",
            description = "Step 3 - Complete Upload to join all files.")
    @PostMapping("/complete")
    public ResponseEntity<ResponseMessage> completeMultipartUpload(
            @RequestBody CompleteVideoUploadRequest completeVideoUploadRequest) {
        log.info(completeVideoUploadRequest);
        var generatedUrlObject = uploadService.completeMultipartUpload(completeVideoUploadRequest);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        log.info(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

