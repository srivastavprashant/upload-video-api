package com.microservice.upload_video_api.controllers;

import com.microservice.upload_video_api.models.dto.PreSign.req.PreSignRequest;
import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.ResponseMessage;
import com.microservice.upload_video_api.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upload-single-part")
@RequiredArgsConstructor
@Tag(name = "Video Upload Method - 1", description = "Video Upload via single part")
public class SinglePartUploadController  {

    final UploadService uploadService;

    @Operation(summary = "Initiate a Upload",
            description = "Step 1 - Initiate Upload. Returns uniqueViewId, send the id with all the files link generated request which is step 2.")
    @PostMapping("/initiate-upload")
    public ResponseEntity<ResponseMessage> initiateSingleUpload(@RequestBody Video videoData) {
        var initiateResponse = uploadService.initiateSingleUpload(videoData);
        ResponseMessage response = new ResponseMessage().withSuccessDefaultResponse(initiateResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "S3 link as response",
            description = "Step 2 - Use URL in response to upload. No completion API required")
    @PostMapping("/generate-pre-signed-url")
    public ResponseEntity<ResponseMessage> generatePreSignedUrl(@RequestBody PreSignRequest preSignRequest) {
        var generatedUrlObject = uploadService.generatePreSignedUrlMultiPartMethod(preSignRequest);
        var response = new ResponseMessage().withSuccessDefaultResponse(generatedUrlObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

