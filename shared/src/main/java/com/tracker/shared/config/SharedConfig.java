package com.tracker.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for shared components.
 */
@Configuration
class SharedConfig {

    /**
     * Provides an ObjectMapper bean for JSON serialization/deserialization.
     * 
     * @return configured ObjectMapper instance
     */
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}