package com.microservice.upload_video_api.models.dto.PreSign.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreSignRequest {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("unique_view_id")
    private String uniqueViewId;
    @JsonProperty("upload_id")
    private String uploadId;
    @JsonProperty("part_count")
    private Integer partCount;
    @JsonProperty("content_length")
    private Long contentLength;
}



