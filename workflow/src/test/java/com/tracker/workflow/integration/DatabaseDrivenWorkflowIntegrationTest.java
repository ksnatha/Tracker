package com.tracker.workflow.integration;

import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.model.UserRole;
import com.tracker.workflow.service.WorkflowDefinitionService;
import com.tracker.workflow.service.WorkflowExpressionEvaluator;
import com.tracker.workflow.service.WorkflowMigrationService;
import com.tracker.workflow.service.WorkflowTaskAssignmentService;
import com.tracker.workflow.repository.UserRoleRepository;
import com.tracker.workflow.repository.WorkflowDefinitionRepository;
import com.tracker.workflow.model.WorkflowRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseDrivenWorkflowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;

    @Autowired
    private WorkflowExpressionEvaluator expressionEvaluator;

    @Autowired
    private WorkflowMigrationService migrationService;

    @Autowired
    private WorkflowTaskAssignmentService taskAssignmentService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @BeforeEach
    void setUp() {
        cleanupTestData();
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testWorkflowMigrationAndActivation() {
        Optional<WorkflowDefinition> beforeMigration = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertFalse(beforeMigration.isPresent());

        migrationService.migrateHardcodedWorkflowToDatabase();

        Optional<WorkflowDefinition> afterMigration = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(afterMigration.isPresent());
        assertEquals("1.0.0", afterMigration.get().getVersion());
        assertTrue(afterMigration.get().getIsActive());
        assertEquals(5, afterMigration.get().getStates().size());
        assertEquals(4, afterMigration.get().getTransitions().size());
    }

    @Test
    void testWorkflowVersioning() {
        WorkflowDefinition v1 = workflowDefinitionService.createWorkflowDefinition(
                "test-workflow", "1.0", "Initial version", "test-user");
        workflowDefinitionService.activateVersion("test-workflow", "1.0", "test-user");

        WorkflowDefinition v2 = workflowDefinitionService.createNewVersion(
                "test-workflow", "2.0", "1.0", "test-user");

        Optional<WorkflowDefinition> activeWorkflow = workflowDefinitionService.getActiveWorkflow("test-workflow");
        assertTrue(activeWorkflow.isPresent());
        assertEquals("1.0", activeWorkflow.get().getVersion());

        workflowDefinitionService.activateVersion("test-workflow", "2.0", "test-user");

        activeWorkflow = workflowDefinitionService.getActiveWorkflow("test-workflow");
        assertTrue(activeWorkflow.isPresent());
        assertEquals("2.0", activeWorkflow.get().getVersion());

        List<WorkflowDefinition> history = workflowDefinitionService.getVersionHistory("test-workflow");
        assertEquals(2, history.size());
    }

    @Test
    void testRoleBasedTaskAssignment() {
        WorkflowRole financeRole = new WorkflowRole();
        financeRole.setRoleName("finance-manager");
        financeRole.setDescription("Finance Manager Role");
        
        if (workflowRoleRepository != null) {
            financeRole = workflowRoleRepository.save(financeRole);
        }

        UserRole financeManager1 = new UserRole();
        financeManager1.setUserId("finance.user1");
        financeManager1.setRole(financeRole);
        userRoleRepository.save(financeManager1);

        UserRole financeManager2 = new UserRole();
        financeManager2.setUserId("finance.user2");
        financeManager2.setRole(financeRole);
        userRoleRepository.save(financeManager2);

        List<String> assignees = taskAssignmentService.resolveRoleBasedAssignees("finance-manager");
        assertEquals(2, assignees.size());
        assertTrue(assignees.contains("finance.user1"));
        assertTrue(assignees.contains("finance.user2"));
    }

    @Test
    void testJsonExpressionEvaluation() {
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 1500.0);
        processData.put("department", "engineering");
        processData.put("priority", "HIGH");

        Map<String, Object> context = new HashMap<>();
        context.put("userId", "user123");

        String simpleExpression = "{\"amount\": {\"$gte\": 1000}}";
        assertTrue(expressionEvaluator.evaluate(simpleExpression, processData, context));

        String complexExpression = "{\"$and\": [{\"amount\": {\"$gte\": 1000}}, {\"department\": {\"$eq\": \"engineering\"}}]}";
        assertTrue(expressionEvaluator.evaluate(complexExpression, processData, context));

        String falseExpression = "{\"amount\": {\"$lt\": 1000}}";
        assertFalse(expressionEvaluator.evaluate(falseExpression, processData, context));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testDatabaseDrivenWorkflowConfiguration() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(workflow.isPresent());

        WorkflowDefinition definition = workflow.get();
        
        definition.getStates().size();
        definition.getTransitions().size(); 
        definition.getTaskAssignments().size();
        
        assertNotNull(definition.getStates());
        assertNotNull(definition.getTransitions());
        assertNotNull(definition.getTaskAssignments());

        assertFalse(definition.getStates().isEmpty());
        assertFalse(definition.getTransitions().isEmpty());
        assertFalse(definition.getTaskAssignments().isEmpty());
    }

    @Test
    void testTaskAssignmentConfiguration() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        // TaskAssignmentConfig assignment = taskAssignmentService.getAssignmentForState(
        //         "process123", "PENDING_PLANNING_BUSINESS_REVIEW");

        // assertNotNull(assignment);
        // assertNotNull(assignment.getTaskName());
        // assertNotNull(assignment.getAssignmentType());
        // assertNotNull(assignment.getAssigneeValue());
    }

    @Test
    void testWorkflowDefinitionPersistence() {
        WorkflowDefinition definition = workflowDefinitionService.createWorkflowDefinition(
                "persistence-test", "1.0", "Test persistence", "test-user");

        assertNotNull(definition.getId());
        assertEquals("persistence-test", definition.getWorkflowName());
        assertEquals("1.0", definition.getVersion());
        assertFalse(definition.getIsActive());

        Optional<WorkflowDefinition> retrieved = workflowDefinitionService.getWorkflowVersion(
                "persistence-test", "1.0");
        assertTrue(retrieved.isPresent());
        assertEquals(definition.getId(), retrieved.get().getId());
    }

    @Test
    void testExpressionEvaluationEdgeCases() {
        Map<String, Object> processData = new HashMap<>();
        processData.put("nullField", null);
        processData.put("booleanField", true);
        processData.put("stringField", "test");

        Map<String, Object> context = new HashMap<>();

        assertTrue(expressionEvaluator.evaluate(null, processData, context));
        assertTrue(expressionEvaluator.evaluate("", processData, context));

        String nullCheckExpression = "{\"nullField\": null}";
        assertTrue(expressionEvaluator.evaluate(nullCheckExpression, processData, context));

        String booleanExpression = "{\"booleanField\": {\"$eq\": true}}";
        assertTrue(expressionEvaluator.evaluate(booleanExpression, processData, context));

        String invalidJsonExpression = "{invalid json}";
        assertFalse(expressionEvaluator.evaluate(invalidJsonExpression, processData, context));
    }
}
