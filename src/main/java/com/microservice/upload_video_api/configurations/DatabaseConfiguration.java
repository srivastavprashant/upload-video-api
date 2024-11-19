package com.microservice.upload_video_api.configurations;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
@Log4j2
public class DatabaseConfiguration {

    private final Database database;

    public DatabaseConfiguration(SecretValueHolder secretValueHolder){
        this.database = secretValueHolder.getDatabase();
    }

    @Bean
    @DependsOn("secretValueHolder")
    public DataSource getDataSource() {
        log.info("Creating data source");
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(database.getClassName());
        dataSourceBuilder.url(database.getUrl());
        dataSourceBuilder.username(database.getUsername());
        dataSourceBuilder.password(database.getPassword());
        return dataSourceBuilder.build();
    }
}