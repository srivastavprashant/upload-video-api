package com.microservice.upload_video_api.configurations;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecretValueHolder {
    private Database database;
    private Ai ai;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Database {
    private String url;
    private String username;
    private String password;
    private String className;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Ai {
    public String chatGptKey;
    public String anthropicKey;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}

