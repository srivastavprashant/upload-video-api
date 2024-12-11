package com.microservice.upload_video_api.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${spring.mvc.servlet.path}")
    private String servletPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server()
                .url(contextPath + servletPath)
                .description("Local server");

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("Video Upload API")
                        .description("API for uploading videos")
                        .version("1.0"));
    }
}

