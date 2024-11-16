package com.microservice.upload_video_api.configurations;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {
    @Configuration
    class S3Configuration {
        @Bean
        public S3Client s3Client() {
            return S3Client.builder().build();
        }

        @Bean
        public S3Presigner s3Presigner() {
            // Create and return an S3Presigner instance
            return S3Presigner.builder().region(Region.AP_SOUTH_1) // Use your desired region
                    .build();
        }
    }

    @Bean
    public SecretsManagerClient secretsManager() {
        return SecretsManagerClient.builder().build();
    }

}
