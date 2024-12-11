package com.microservice.upload_video_api.services_impl;


import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
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
    static String DIR;
    final S3Client s3Client;
    final S3Presigner s3Presigner;
    private final S3UploadedVideoDescriptionDataRepository s3UploadedVideoDescriptionDataRepository;

    static {
        DIR = "uploaded-videos/";
    }

    @PostConstruct
    private void init() {
        var file = new File(DIR);
        if (!file.exists()) {
            file.mkdir();
            log.info("Directory created: {}", file.getAbsolutePath());
        } else {
            log.info("Folder exists");
        }
    }

    @Override
    @SneakyThrows
    public VideoEntity saveVideo(Video video, MultipartFile multipartFile) {
        log.info(video);
        var fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        var contentType = multipartFile.getContentType();
        var inputStream = multipartFile.getInputStream();

        var folderName = DIR;
        var path = Paths.get(folderName, fileName);

        //todo replace with s3 upload
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        var videoEntity = new VideoEntity().from(video);
        videoEntity.setVideoUrl(path.toString());
        return videoRepository.save(videoEntity);

    }

    public UploadInitiateResponse initiateMultipartUpload(InitiateUploadRequest initiateUploadRequest) {
        // Constants
        final String UPLOAD_STATUS_IN_PROGRESS = "InProgress";
        final Duration EXPIRY_DURATION = Duration.ofHours(24);

        // Generate file path
        String filePath = folderName + initiateUploadRequest.getFileName();

        // Create expiry date once
        Instant expiryDate = Instant.now().plus(EXPIRY_DURATION);

        // Build video entity with all properties at once
        VideoEntity videoEntity = new VideoEntity()
                .from(initiateUploadRequest.getVideoData());
        videoEntity.setIsUploaded(UPLOAD_STATUS_IN_PROGRESS);
        videoEntity.setExpiryDateOfUploadId(expiryDate.atZone(ZoneId.systemDefault()).toLocalDateTime());

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
                videoEntity.getUniqueViewId(),
                response.abortDate()
        );
    }


    public Map<String, String> generatePreSignedUrl(String fileName, String uploadId, int partNumber, Long contentLength) {
        try {
            // Create the upload part request
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(s3BucketName)
                    .key(folderName + fileName)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength(contentLength) // Set exact content length for each part as needed
                    .build();

            // Create the pre signed request for uploading a part
            UploadPartPresignRequest preSignRequest = UploadPartPresignRequest.builder()
                    .uploadPartRequest(uploadPartRequest) // Provide the UploadPartRequest directly
                    .signatureDuration(Duration.ofHours(24)) // Set the pre signed URL expiration time
                    .build();

            // Generate the pre signed URL
            var preSignedResponse = s3Presigner.presignUploadPart(preSignRequest);
            // Prepare the response map
            Map<String, String> response = new HashMap<>();
            response.put("url", preSignedResponse.url().toString());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public Map<String, String> completeMultipartUpload(String fileName, String uploadId, List<ETagList> eTagList) {
        // Create a list of completed parts
        List<CompletedPart> completedPartsList = eTagList.parallelStream()
                .map(part -> CompletedPart.builder()
                        .partNumber(part.partNumber())
                        .eTag(part.eTag())
                        .build())
                .toList();

        // Create the complete multipart upload request
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(s3BucketName)
                .key(folderName + fileName)
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
}