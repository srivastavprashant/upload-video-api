package com.microservice.upload_video_api.configurations;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class AWSConfiguration {
    @Configuration
    static
    class S3Configuration {
        @Bean
        public S3Client s3Client() {
            log.info("Creating S3 client");
            return S3Client.builder().build();
        }

        @Bean
        public S3Presigner s3Presigner() {
            log.info("Creating S3 pre-signer");
            return S3Presigner.builder().region(Region.AP_SOUTH_1) // Use your desired region
                    .build();
        }
    }

    @Bean
    public SecretsManagerClient secretsManager() {
        return SecretsManagerClient.builder().build();
    }

}
