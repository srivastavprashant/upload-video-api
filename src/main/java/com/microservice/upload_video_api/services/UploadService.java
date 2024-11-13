package com.microservice.upload_video_api.services;

import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.entities.VideoEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
//    for saving video, basic method
    VideoEntity saveVideo(Video video, MultipartFile multipartFile);

}
