package com.tracker.workflow.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Test configuration for integration tests.
 * This class configures the database connection and other test-specific settings.
 */
@TestConfiguration
/*@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.odyssey"})*/
//@SpringBootApplication(scanBasePackages = "com.tracker")
public class TestConfig {

    /**
     * Configure a test data source that connects to a local PostgreSQL database.
     * 
     * @return DataSource configured for tests
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tracker_db");
        dataSource.setUsername("tracker_test");
        dataSource.setPassword("tracker_test_123");
        
        return dataSource;
    }
}