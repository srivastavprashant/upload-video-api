package com.microservice.upload_video_api.services_impl;



import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.dto.UploadInitiateResponse;
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

    static{
        DIR = "uploaded-videos/";
    }

    @PostConstruct
    private void init() {
        var file = new File(DIR);
        if(!file.exists()) {
            file.mkdir();
            log.info("Directory created: {}", file.getAbsolutePath());
        }
        else{
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

    public UploadInitiateResponse initiateMultipartUpload(String fileName, String contentType, Video videoData) {
        var videoEntity = new VideoEntity().from(videoData);
        var expiryDate = Instant.now().plus(Duration.ofHours(24));
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(s3BucketName)
                .key(folderName + fileName)
                .expires(expiryDate) //expire after 24 hrs.
                .contentType(contentType)
                .build();
        videoEntity.setVideoUrl(folderName + fileName);
        videoEntity.setExpiryDateOfUploadId(expiryDate.atZone(ZoneId.systemDefault()).toLocalDateTime());
        videoEntity.setIsUploaded("InProgress");
        videoRepository.save(videoEntity);
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
        return new UploadInitiateResponse(fileName, videoEntity.getUniqueViewId(), response.abortDate());
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