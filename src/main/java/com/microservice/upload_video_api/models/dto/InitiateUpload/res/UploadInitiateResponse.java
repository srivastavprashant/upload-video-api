package com.microservice.upload_video_api.models.dto.InitiateUpload.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadInitiateResponse {
    @JsonProperty("file_name")
    private String filename;

    @JsonProperty("upload_id")
    private String uploadId;

    @JsonProperty("expiry_date")
    private Instant expiryDate;

    @JsonProperty("unique_view_id")
    private String uniqueViewId;;
}
