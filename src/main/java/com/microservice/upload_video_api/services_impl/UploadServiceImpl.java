package com.microservice.upload_video_api.services_impl;


import com.microservice.upload_video_api.models.dto.CompleteVideoUpload.req.CompleteVideoUploadRequest;
import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
import com.microservice.upload_video_api.models.dto.PreSign.req.PreSignRequest;
import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.dto.InitiateUpload.res.UploadInitiateResponse;
import com.microservice.upload_video_api.models.entities.S3UploadedVideoDescriptionData;
import com.microservice.upload_video_api.models.entities.VideoEntity;
import com.microservice.upload_video_api.repositories.S3UploadedVideoDescriptionDataRepository;
import com.microservice.upload_video_api.repositories.VideoRepository;
import com.microservice.upload_video_api.services.UploadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;


import static com.microservice.upload_video_api.configurations.constants.ApplicationConstants.folderName;
import static com.microservice.upload_video_api.configurations.constants.ApplicationConstants.s3BucketName;


@RequiredArgsConstructor
@Service
@Log4j2
public class UploadServiceImpl implements UploadService {

    final VideoRepository videoRepository;
    final S3Client s3Client;
    final S3Presigner s3Presigner;
    private final S3UploadedVideoDescriptionDataRepository s3UploadedVideoDescriptionDataRepository;

    public UploadInitiateResponse initiateMultipartUpload(InitiateUploadRequest initiateUploadRequest) {
        // Constants
        final String UPLOAD_STATUS_IN_PROGRESS = "InProgress";
        final Duration EXPIRY_DURATION = Duration.ofHours(24);

        // Create expiry date once
        Instant expiryDate = Instant.now().plus(EXPIRY_DURATION);

        // Build video entity with all properties at once
        VideoEntity videoEntity = new VideoEntity()
                .from(initiateUploadRequest.getVideoData());
        videoEntity.setIsUploaded(UPLOAD_STATUS_IN_PROGRESS);
        videoEntity.setExpiryDateOfUploadId(expiryDate.atZone(ZoneId.systemDefault()).toLocalDateTime());

        // Generate file path and file extension
        var fileExtension = initiateUploadRequest.getFileName().substring(initiateUploadRequest.getFileName().lastIndexOf("."));
        String filePath = folderName + videoEntity.getId() + fileExtension;

        // Create S3 upload request
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(s3BucketName)
                .key(filePath)
                .expires(expiryDate)
                .contentType(initiateUploadRequest.getContentType())
                .build();

        // Execute operations in parallel using CompletableFuture
        CompletableFuture<VideoEntity> saveEntityFuture = CompletableFuture
                .supplyAsync(() -> videoRepository.save(videoEntity));

        CompletableFuture<CreateMultipartUploadResponse> uploadFuture = CompletableFuture
                .supplyAsync(() -> s3Client.createMultipartUpload(createMultipartUploadRequest));

        // Wait for both operations to complete
        CompletableFuture.allOf(saveEntityFuture, uploadFuture).join();

        CreateMultipartUploadResponse response = uploadFuture.join();

        return new UploadInitiateResponse(
                initiateUploadRequest.getFileName(),
                response.uploadId(),
                response.abortDate(),
                videoEntity.getUniqueViewId()
        );
    }


    public Map<String, String> generatePreSignedUrlMultiPartMethod(PreSignRequest preSignRequest) {
        try {
            var fileId = videoRepository.findByUniqueViewId(preSignRequest.getUniqueViewId()).getId();
            // Create the upload part request
            var fileExtension = preSignRequest.getFileName().substring(preSignRequest.getFileName().lastIndexOf("."));
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(s3BucketName)
                    .key(folderName + fileId + fileExtension)
                    .uploadId(preSignRequest.getUploadId())
                    .partNumber(preSignRequest.getPartCount())
                    .contentLength(preSignRequest.getContentLength()) // Set exact content length for each part as needed
                    .build();

            // Create the pre signed request for uploading a part
            UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest) // Provide the UploadPartRequest directly
                    .signatureDuration(Duration.ofHours(24)) // Set the pre signed URL expiration time
                    .build();

            // Generate the pre signed URL
            var preSignedResponse = s3Presigner.presignUploadPart(uploadPartPresignRequest);
            // Prepare the response map
            Map<String, String> response = new HashMap<>();
            response.put("url", preSignedResponse.url().toString());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public Map<String, String> completeMultipartUpload(CompleteVideoUploadRequest completeVideoUploadRequest) {
        // Create a list of completed parts
        var eTagList = completeVideoUploadRequest.getEtags();
        var fileExtension = completeVideoUploadRequest.getFileName().substring(completeVideoUploadRequest.getFileName().lastIndexOf("."));
        var fileId = videoRepository.findByUniqueViewId(completeVideoUploadRequest.getUniqueViewId()).getId();
        var uploadId = completeVideoUploadRequest.getUploadId();
        List<CompletedPart> completedPartsList = eTagList.stream()
                .map(part -> CompletedPart.builder()
                        .partNumber(part.getPartNumber())
                        .eTag(part.getETag())
                        .build())
                .sorted(Comparator.comparing(CompletedPart::partNumber))
                .toList();


        // Create the complete multipart upload request
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(s3BucketName)
                .key(folderName + fileId + fileExtension)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                        .parts(completedPartsList)
                        .build())
                .build();

        // Complete the multipart upload
        CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeMultipartUploadRequest);
        var s3UploadedVideoDescriptionData = new S3UploadedVideoDescriptionData();
        s3UploadedVideoDescriptionData.setS3UploadedVideoDescriptionDataId(String.valueOf(UUID.randomUUID()));
        BeanUtils.copyProperties(response, s3UploadedVideoDescriptionData);
        var savedData = s3UploadedVideoDescriptionDataRepository.save(s3UploadedVideoDescriptionData);

        // Prepare the response map
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Upload completed successfully");
        return responseMap;
    }

    public Map<String, String> initiateSingleUpload(Video videoData) {
        final String UPLOAD_STATUS_IN_PROGRESS = "InProgress";
        // Build video entity with all properties at once
        VideoEntity videoEntity = new VideoEntity()
                .from(videoData);
        videoEntity.setIsUploaded(UPLOAD_STATUS_IN_PROGRESS);
        var savedEntity = videoRepository.save(videoEntity);
        // Prepare the response
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("unique_video_id", savedEntity.getUniqueViewId());
        return responseMap;
    }

    public Map<String, String> generatePreSignedUrl(PreSignRequest preSignRequest) {
        try {
            String dbId = videoRepository.findByUniqueViewId(preSignRequest.getUniqueViewId()).getId();
            // Create folder structure
            String folderNameRemote = folderName + dbId + "/";
            String objectKey = folderNameRemote + preSignRequest.getFileName();

            // Create a preSigned URL
            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15)) // URL expires in 15 minutes
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(s3BucketName)
                            .key(objectKey)
                            .build())
                    .build();

            var preSignedRequest = S3Presigner.create().presignPutObject(putObjectPresignRequest);
            String preSignedUrl = preSignedRequest.url().toString();

            // Prepare the response
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("video_id", preSignRequest.getUniqueViewId());
            responseMap.put("file_name", preSignRequest.getFileName());
            responseMap.put("url", preSignedUrl);
            responseMap.put("object_key", objectKey);

            return responseMap;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }
}
