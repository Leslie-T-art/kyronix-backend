package com.kyronic.riskengine.dashboard.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DashboardDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties platformDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    DataSource platformDataSource(@Qualifier("platformDataSourceProperties") DataSourceProperties platformDataSourceProperties) {
        return platformDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("dashboard.auth-datasource")
    DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    DataSource authDataSource(@Qualifier("authDataSourceProperties") DataSourceProperties authDataSourceProperties) {
        return DataSourceBuilder.create()
                .url(authDataSourceProperties.getUrl())
                .username(authDataSourceProperties.getUsername())
                .password(authDataSourceProperties.getPassword())
                .driverClassName(authDataSourceProperties.getDriverClassName())
                .build();
    }

    @Bean
    @Primary
    NamedParameterJdbcTemplate platformJdbcTemplate(@Qualifier("platformDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    NamedParameterJdbcTemplate authJdbcTemplate(@Qualifier("authDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
