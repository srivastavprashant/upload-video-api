package com.microservice.upload_video_api.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class S3UploadedVideoDescriptionData {
    @Id
    private String s3UploadedVideoDescriptionDataId;

    private String location;

    private String bucket;

    private String key;

    private String expiration;

    private String eTag;

    private String checksumCRC32;

    private String checksumCRC32C;

    private String checksumSHA1;

    private String checksumSHA256;

    private String serverSideEncryption;

    private String versionId;

    private String ssekmsKeyId;

    private Boolean bucketKeyEnabled;

    private String requestCharged;

}
