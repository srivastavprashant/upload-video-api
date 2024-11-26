package com.microservice.upload_video_api.models.entities;

import com.microservice.upload_video_api.models.dto.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
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
    @Id
    private String uniqueViewId;
    private String uploadS3Id;
    private LocalDateTime expiryDateOfUploadId;
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
    private String isUploaded;

    public VideoEntity from(Video video){
        //todo, better way of copying, as this way we will also replace id(generated) sent by client side as per their choice, this shoulnt appen
        BeanUtils.copyProperties(video, this);
        this.id = UUID.randomUUID().toString();
        this.uniqueViewId = UUID.randomUUID().toString();
        return this;
    }
}
