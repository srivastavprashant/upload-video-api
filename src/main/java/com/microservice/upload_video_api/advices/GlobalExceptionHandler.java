package com.microservice.upload_video_api.advices;

import com.microservice.upload_video_api.models.ResponseMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception e) {
        log.error("Exception Occurred {} at {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        var response = new ResponseMessage()
            .withErrorDefaultResponse(null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}