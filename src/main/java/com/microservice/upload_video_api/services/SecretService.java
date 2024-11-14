package com.microservice.upload_video_api.services;

import com.microservice.upload_video_api.configurations.SecretValueHolder;

public interface SecretService {
    // Secret Service to get critical data
    SecretValueHolder secretValueHolder();
}
