package com.microservice.upload_video_api.services_impl;



import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.entities.VideoEntity;
import com.microservice.upload_video_api.repositories.VideoRepository;
import com.microservice.upload_video_api.services.UploadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@RequiredArgsConstructor
@Service
@Log4j2
public class UploadServiceImpl implements UploadService {

    final VideoRepository videoRepository;
    static String DIR;

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


}

