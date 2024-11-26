package com.microservice.upload_video_api.repositories;


import com.microservice.upload_video_api.models.entities.S3UploadedVideoDescriptionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface S3UploadedVideoDescriptionDataRepository extends JpaRepository<S3UploadedVideoDescriptionData, String> {

}
