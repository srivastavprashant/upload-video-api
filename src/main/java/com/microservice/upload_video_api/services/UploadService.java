package com.microservice.upload_video_api.services;

import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.dto.InitiateUpload.res.UploadInitiateResponse;
import com.microservice.upload_video_api.models.entities.VideoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UploadService {
    //    for saving video, basic method
    VideoEntity saveVideo(Video video, MultipartFile multipartFile);

    UploadInitiateResponse initiateMultipartUpload(InitiateUploadRequest initiateUploadRequest);

    Map<String, String> generatePreSignedUrl(String fileName, String uploadId, int partNumber, Long contentLength);

    Map<String, String> completeMultipartUpload(String fileName, String uploadId, List<ETagList> etagList);
}
