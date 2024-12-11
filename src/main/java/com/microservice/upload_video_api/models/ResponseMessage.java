package com.microservice.upload_video_api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage {
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private Object data;
    @JsonProperty("internal_status_code")
    private String internalStatusCode;
    @JsonProperty("exception_occurred")
    private String exceptionOccurred;
    @JsonProperty("exception_message")
    private String exceptionMessage;

    public ResponseMessage withSuccessDefaultResponse(Object resultantResponse) {
        this.message = "Success";
        this.data = resultantResponse;
        this.internalStatusCode = "100";
        this.exceptionOccurred = "N";
        this.exceptionMessage = "";
        return this;
    }
    public ResponseMessage withErrorDefaultResponse(Object resultantResponse) {
        this.message = "Failed";
        this.data = resultantResponse;
        this.internalStatusCode = "500";
        this.exceptionOccurred = "Y";
        this.exceptionMessage = "Processing Failed";
        return this;
    }
}
