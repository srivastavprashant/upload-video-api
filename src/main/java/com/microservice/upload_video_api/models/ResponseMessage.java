package com.microservice.upload_video_api.models;

import com.microservice.upload_video_api.dto.Video;
import com.microservice.upload_video_api.entities.VideoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage {
    private String message;
    private Object data;
    private String internalStatusCode;
    private String exceptionOccurred;
    private String exceptionMessage;
}
