package com.tracker.workflow.integration;


import com.tracker.bootstrap.TrackerBootstrapApplication;
import com.tracker.workflow.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    protected WorkflowTaskRepository workflowTaskRepository;
    
    @Autowired(required = false)
    protected TaskGroupRepository taskGroupRepository;
    
    @Autowired(required = false)
    protected ProcessHistoryRepository processHistoryRepository;
    
    @Autowired(required = false)
    protected WorkflowRuleRepository workflowRuleRepository;
    
    @Autowired(required = false)
    protected WorkflowDefinitionRepository workflowDefinitionRepository;
    
    @Autowired(required = false)
    protected WorkflowStateDefinitionRepository workflowStateDefinitionRepository;
    
    @Autowired(required = false)
    protected WorkflowTransitionDefinitionRepository workflowTransitionDefinitionRepository;
    
    @Autowired(required = false)
    protected WorkflowTaskAssignmentRepository workflowTaskAssignmentRepository;
    
    @Autowired(required = false)
    protected UserRoleRepository userRoleRepository;
    
    @Autowired(required = false)
    protected WorkflowRoleRepository workflowRoleRepository;

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
        if (taskGroupRepository != null) {
            taskGroupRepository.deleteAll();
        }
        if (workflowTaskRepository != null) {
            workflowTaskRepository.deleteAll();
        }
        if (processHistoryRepository != null) {
            processHistoryRepository.deleteAll();
        }
        if (workflowRuleRepository != null) {
            workflowRuleRepository.deleteAll();
        }
        
        if (workflowTaskAssignmentRepository != null) {
            workflowTaskAssignmentRepository.deleteAll();
        }
        if (workflowTransitionDefinitionRepository != null) {
            workflowTransitionDefinitionRepository.deleteAll();
        }
        if (workflowStateDefinitionRepository != null) {
            workflowStateDefinitionRepository.deleteAll();
        }
        if (workflowDefinitionRepository != null) {
            workflowDefinitionRepository.deleteAll();
        }
        if (userRoleRepository != null) {
            userRoleRepository.deleteAll();
        }
        if (workflowRoleRepository != null) {
            workflowRoleRepository.deleteAll();
        }
    }
}
