package com.tracker.workflow.integration;

import com.tracker.bootstrap.TrackerBootstrapApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {TrackerBootstrapApplication.class, TestConfig.class})
@ActiveProfiles("test")
public class SmokeTest {
        @Test
        void contextLoads() {}
}
