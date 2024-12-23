package com.microservice.upload_video_api.models.dto.CompleteVideoUpload.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Data
@lombok.RequiredArgsConstructor
public class CompleteVideoUploadRequest {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("unique_video_id")
    private String uniqueViewId;
    @JsonProperty("upload_id")
    private String uploadId;
    @JsonProperty("e_tags")
    private List<ETag> etags;

    @lombok.Data
    @lombok.RequiredArgsConstructor
    public static class ETag {
        @JsonProperty("e_tag")
        private String eTag;
        @JsonProperty("part_number")
        private int partNumber;
    }
}