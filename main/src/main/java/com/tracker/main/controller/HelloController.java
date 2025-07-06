package com.tracker.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.tracker.main.config.ApplicationProperties;

/**
 * A simple controller to test the API
 */
@RestController
@Log4j2
@RequiredArgsConstructor
class HelloController {

    private final ApplicationProperties applicationProperties;

    @GetMapping("/hello")
    String hello() {
        log.info("Hello endpoint called from {}, version {}", 
                applicationProperties.getName(), 
                applicationProperties.getVersion());
        return "hello from com.odyssey package";
    }
}