package com.tracker.workflow.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

/**
 * Test configuration for integration tests.
 * This class configures the database connection using Testcontainers PostgreSQL.
 */
@TestConfiguration
@SpringBootApplication(scanBasePackages = "com.tracker")
@Testcontainers
public class TestConfig {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tracker_test")
            .withUsername("tracker_test")
            .withPassword("tracker_test_123");

    static {
        postgres.start();
    }

    /**
     * Configure a test data source that connects to Testcontainers PostgreSQL.
     * 
     * @return DataSource configured for tests
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        
        return dataSource;
    }
}
