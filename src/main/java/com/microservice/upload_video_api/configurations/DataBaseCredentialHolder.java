package com.microservice.upload_video_api.configurations;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataBaseCredentialHolder {
    private String databaseUrl;
    private String username;
    private String password;
    private String driverClassName;
}
