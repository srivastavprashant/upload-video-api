package com.microservice.upload_video_api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage {
    private String message;
    private Object data;
    private String internalStatusCode;
    private String exceptionOccurred;
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
