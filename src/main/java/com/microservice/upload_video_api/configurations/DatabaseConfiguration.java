package com.microservice.upload_video_api.configurations;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    private final Database database;

    public DatabaseConfiguration(SecretValueHolder secretValueHolder){
        this.database = secretValueHolder.getDatabase();
    }

    @Bean
    @DependsOn("secretValueHolder")
    public DataSource getDataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(database.getClassName());
        dataSourceBuilder.url(database.getUrl());
        dataSourceBuilder.username(database.getUsername());
        dataSourceBuilder.password(database.getPassword());
        return dataSourceBuilder.build();
    }
}