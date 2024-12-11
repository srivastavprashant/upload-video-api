package com.microservice.upload_video_api.models.dto.InitiateUpload.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservice.upload_video_api.models.dto.Video;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiateUploadRequest {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("video_data")
    private Video videoData;
}