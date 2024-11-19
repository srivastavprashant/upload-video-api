package com.microservice.upload_video_api.dto.records;

import java.time.Instant;

public record UploadInitiateResponse(String filename, String uploadId, Instant expiryDate) {
}
