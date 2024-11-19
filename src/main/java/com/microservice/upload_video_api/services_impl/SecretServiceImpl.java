package com.microservice.upload_video_api.services_impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.upload_video_api.configurations.SecretManagerSecretHolder;
import com.microservice.upload_video_api.configurations.SecretValueHolder;
import com.microservice.upload_video_api.services.SecretService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Service
@RequiredArgsConstructor
@Log4j2
public class SecretServiceImpl implements SecretService {

    private final SecretManagerSecretHolder secretManagerSecretHolder;
    private final SecretsManagerClient secretsManagerClient;

    @Bean
    public SecretValueHolder secretValueHolder() {
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretManagerSecretHolder.getAllSecretHolder())
                .build();
        log.info("Secrets Manager call initiated to fetch secret value");
        log.info("Current active environment: {}", secretManagerSecretHolder.getRunningEnvironment());
        var getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var secretValueHolder = getSecretValueResponse.secretString();
            var rootNode = objectMapper.readTree(secretValueHolder);
            String env;
            switch (secretManagerSecretHolder.getRunningEnvironment()) {
                case "uat" -> env = rootNode.path("uat").asText();
                case "prod" -> env = rootNode.path("prod").asText();
                default -> env = rootNode.path("dev").asText();
            }
            return objectMapper.readValue(env, SecretValueHolder.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret-manager JSON", e);
        }

    }
}

