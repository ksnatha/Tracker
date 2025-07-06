package com.tracker.workflow.integration;

import com.tracker.bootstrap.TrackerBootstrapApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TrackerBootstrapApplication.class, TestConfig.class})
public class SmokeTest {
        @Test
        void contextLoads() {}
}
