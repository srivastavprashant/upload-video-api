package com.microservice.upload_video_api.entities;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.microservice.upload_video_api.dto.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class VideoEntity {
    @Id
    private String id;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private String uploadDate;
    private String status;
    private String userIdOfOwner;
    private String categoryId;
    private String tags;
    private String viewCount;
    private String likeCount;
    private String dislikeCount;
    private String commentCount;
    private String duration;
    private String uploadType;
    private String uploadSize;

    public VideoEntity from(Video video){

        BeanUtils.copyProperties(video, this);
        this.id = UUID.randomUUID().toString();
        return this;
    }
}
