package com.microservice.upload_video_api.models.dto;


public record Video(
        String id,
        String title,
        String description,
        String videoUrl,
        String thumbnailUrl,
        String uploadDate,
        String status,
        String userIdOfOwner,
        String categoryId,
        String tags,
        String viewCount,
        String likeCount,
        String dislikeCount,
        String commentCount,
        String duration,
        String uploadType,
        String uploadSize
) {

}

