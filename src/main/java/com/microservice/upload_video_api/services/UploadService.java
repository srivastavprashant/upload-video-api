package com.microservice.upload_video_api.services;

import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.dto.records.UploadInitiateResponse;
import com.microservice.upload_video_api.entities.VideoEntity;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.List;
import java.util.Map;

public interface UploadService {
    //    for saving video, basic method
    VideoEntity saveVideo(Video video, MultipartFile multipartFile);

    UploadInitiateResponse initiateMultipartUpload(String fileName);

    Map<String, String> generatePreSignedUrl(String fileName, String uploadId, int partNumber);

    Map<String, String> completeMultipartUpload(String fileName, String uploadId, List<CompletedPart> completedParts);
}
