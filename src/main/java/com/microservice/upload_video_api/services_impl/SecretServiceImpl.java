package com.microservice.upload_video_api.services_impl;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.upload_video_api.configurations.SecretManagerSecretHolder;
import com.microservice.upload_video_api.configurations.SecretValueHolder;
import com.microservice.upload_video_api.services.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecretServiceImpl implements SecretService {

    private final SecretManagerSecretHolder secretManagerSecretHolder;
    private final AWSSecretsManager awsSecretsManager;

    @Bean
    public SecretValueHolder secretValueHolder() {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(secretManagerSecretHolder.getAllSecretHolder());
        var getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var rootNode = objectMapper.readTree(getSecretValueResult.getSecretString());

            String env;
            switch (secretManagerSecretHolder.getRunningEnvironment()) {
                case "uat" -> env = rootNode.path("uat").asText();
                case "prod" -> env = rootNode.path("prod").asText();
                default -> env = rootNode.path("dev").asText();
            }


            SecretValueHolder secretValueHolder = new SecretValueHolder();
            return objectMapper.readValue(env, SecretValueHolder.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret JSON", e);
        }

    }
}

