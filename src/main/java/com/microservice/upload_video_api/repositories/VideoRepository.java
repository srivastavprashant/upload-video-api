package com.microservice.upload_video_api.repositories;

import com.microservice.upload_video_api.entities.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, String> {
}
