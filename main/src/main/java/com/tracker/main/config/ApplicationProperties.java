package com.tracker.main.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Configuration properties for the application.
 */
@ConfigurationProperties(prefix = "app.tracker")
@Validated
@Getter
@RequiredArgsConstructor
public class ApplicationProperties {

    /**
     * The name of the application.
     */
    @NotBlank
    private final String name;

    /**
     * The version of the application.
     */
    @NotBlank
    private final String version;
}