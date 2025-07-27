package com.tracker.workflow.integration;


import com.tracker.bootstrap.TrackerBootstrapApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base class for integration tests.
 * This class sets up the test environment and provides common functionality for all integration tests.
 */
@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes ={TrackerBootstrapApplication.class, TestConfig.class})
@SpringBootTest(classes = {TrackerBootstrapApplication.class, TestConfig.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
/*@ComponentScan(basePackages = {
        "com.tracker.main",
        "com.tracker.shared",
        "com.tracker.workflow"
})*/
public abstract class AbstractIntegrationTest {

    // Common test setup and utility methods can be added here

    /**
     * Helper method to create test data for workflow tests.
     * 
     * @param userId The ID of the user initiating the workflow
     * @return A map of process data for testing
     */
    protected java.util.Map<String, Object> createTestProcessData(String userId) {
        java.util.Map<String, Object> processData = new java.util.HashMap<>();
        processData.put("requestType", "EXPENSE");
        processData.put("amount", 1000.0);
        processData.put("description", "Test expense request");
        processData.put("department", "Engineering");
        processData.put("submittedBy", userId);
        processData.put("submittedDate", java.time.LocalDateTime.now().toString());
        return processData;
    }

    protected java.util.Map<String, Object> createTestProcessDataForWFTransistion(String userId,String comment) {
        java.util.Map<String, Object> processData = new java.util.HashMap<>();
        processData.put("comment", comment);
        processData.put("submittedBy", userId);
        processData.put("submittedDate", java.time.LocalDateTime.now().toString());
        return processData;
    }

    protected void cleanupTestData() {
    }
}
