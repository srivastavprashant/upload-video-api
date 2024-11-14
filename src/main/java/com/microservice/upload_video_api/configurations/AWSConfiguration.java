package com.microservice.upload_video_api.configurations;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {
    @Bean
    public S3Client s3Client() {
        return S3Client.builder().build();
    }
    @Bean
    public SecretsManagerClient secretsManager() {
        return SecretsManagerClient.builder().build();
    }

}
