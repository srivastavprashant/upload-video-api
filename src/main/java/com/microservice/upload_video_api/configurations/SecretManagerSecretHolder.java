package com.microservice.upload_video_api.configurations;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("secret-manager")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SecretManagerSecretHolder {
    // due to limiting cost involved in personal development, creating 1 secret
    private String allSecretHolder;
    private String runningEnvironment;
}
