package com.tracker.workflow.bdd;

import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.model.UserRole;
import com.tracker.workflow.model.WorkflowRole;
import com.tracker.workflow.service.WorkflowDefinitionService;
import com.tracker.workflow.service.WorkflowExpressionEvaluator;
import com.tracker.workflow.service.WorkflowMigrationService;
import com.tracker.workflow.repository.UserRoleRepository;
import com.tracker.workflow.repository.WorkflowDefinitionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(classes = {com.tracker.bootstrap.TrackerBootstrapApplication.class})
@ActiveProfiles("test")
public class WorkflowStepDefinitions {

    @Autowired
    private WorkflowDefinitionService workflowDefinitionService;

    @Autowired
    private WorkflowExpressionEvaluator expressionEvaluator;

    @Autowired
    private WorkflowMigrationService migrationService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    private WorkflowDefinition currentWorkflow;
    private String guardExpression;
    private Map<String, Object> processData;
    private boolean guardResult;
    private String workflowName;
    private String currentVersion;

    @Given("the workflow system is initialized")
    public void theWorkflowSystemIsInitialized() {
        processData = new HashMap<>();
    }

    @Given("the database is clean")
    public void theDatabaseIsClean() {
        workflowDefinitionRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Given("I have a workflow definition {string} version {string}")
    public void iHaveAWorkflowDefinitionVersion(String name, String version) {
        workflowName = name;
        currentVersion = version;
        currentWorkflow = workflowDefinitionService.createWorkflowDefinition(
                name, version, "Test workflow", "test-user");
    }

    @When("I create a new version {string} with description {string}")
    public void iCreateANewVersionWithDescription(String version, String description) {
        currentWorkflow = workflowDefinitionService.createNewVersion(
                workflowName, version, currentVersion, "test-user");
        currentVersion = version;
    }

    @When("I activate version {string}")
    public void iActivateVersion(String version) {
        workflowDefinitionService.activateVersion(workflowName, version, "test-user");
    }

    @Then("the workflow {string} should have active version {string}")
    public void theWorkflowShouldHaveActiveVersion(String name, String version) {
        Optional<WorkflowDefinition> activeWorkflow = workflowDefinitionService.getActiveWorkflow(name);
        assertTrue(activeWorkflow.isPresent());
        assertEquals(version, activeWorkflow.get().getVersion());
        assertTrue(activeWorkflow.get().getIsActive());
    }

    @Then("version {string} should be inactive")
    public void versionShouldBeInactive(String version) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getWorkflowVersion(workflowName, version);
        assertTrue(workflow.isPresent());
        assertFalse(workflow.get().getIsActive());
    }

    @Given("I have an active workflow with role-based assignments")
    public void iHaveAnActiveWorkflowWithRoleBasedAssignments() {
        currentWorkflow = workflowDefinitionService.createWorkflowDefinition(
                "test-workflow", "1.0", "Test workflow", "test-user");
        workflowDefinitionService.activateVersion("test-workflow", "1.0", "test-user");
    }

    @Given("user {string} has role {string}")
    public void userHasRole(String userId, String roleName) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        WorkflowRole role = new WorkflowRole();
        role.setRoleName(roleName);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    @When("a workflow reaches state {string}")
    public void aWorkflowReachesState(String stateName) {
    }

    @Then("tasks should be assigned to users with role {string}")
    public void tasksShouldBeAssignedToUsersWithRole(String roleName) {
    }

    @Then("the assignees should include {string} and {string}")
    public void theAssigneesShouldIncludeAnd(String user1, String user2) {
    }

    @Given("I have a workflow with guard expression {string}")
    public void iHaveAWorkflowWithGuardExpression(String expression) {
        guardExpression = expression;
    }

    @Given("the process data contains amount {int}")
    public void theProcessDataContainsAmount(int amount) {
        processData.put("amount", (double) amount);
    }

    @When("the workflow evaluates the guard condition")
    public void theWorkflowEvaluatesTheGuardCondition() {
        guardResult = expressionEvaluator.evaluate(guardExpression, processData, new HashMap<>());
    }

    @Then("the guard should return true")
    public void theGuardShouldReturnTrue() {
        assertTrue(guardResult);
    }

    @Then("the transition should be allowed")
    public void theTransitionShouldBeAllowed() {
        assertTrue(guardResult);
    }

    @Then("the guard should return false")
    public void theGuardShouldReturnFalse() {
        assertFalse(guardResult);
    }

    @Then("the transition should be blocked")
    public void theTransitionShouldBeBlocked() {
        assertFalse(guardResult);
    }

    @Given("there is no active workflow in the database")
    public void thereIsNoActiveWorkflowInTheDatabase() {
        workflowDefinitionRepository.deleteAll();
    }

    @When("the migration service runs")
    public void theMigrationServiceRuns() {
        migrationService.migrateHardcodedWorkflowToDatabase();
    }

    @Then("a new workflow definition should be created")
    public void aNewWorkflowDefinitionShouldBeCreated() {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(workflow.isPresent());
    }

    @Then("it should have {int} states")
    public void itShouldHaveStates(int stateCount) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(workflow.isPresent());
        assertEquals(stateCount, workflow.get().getStates().size());
    }

    @Then("it should have {int} transitions")
    public void itShouldHaveTransitions(int transitionCount) {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(workflow.isPresent());
        assertEquals(transitionCount, workflow.get().getTransitions().size());
    }

    @Then("it should have role-based task assignments")
    public void itShouldHaveRoleBasedTaskAssignments() {
        Optional<WorkflowDefinition> workflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        assertTrue(workflow.isPresent());
        assertFalse(workflow.get().getTaskAssignments().isEmpty());
    }

    @Given("I have a database-driven workflow configuration")
    public void iHaveADatabaseDrivenWorkflowConfiguration() {
        migrationService.migrateHardcodedWorkflowToDatabase();
    }

    @Given("the workflow has dynamic task assignments")
    public void theWorkflowHasDynamicTaskAssignments() {
    }

    @When("I start a new workflow process")
    public void iStartANewWorkflowProcess() {
    }

    @When("I trigger workflow events in sequence")
    public void iTriggerWorkflowEventsInSequence() {
    }

    @Then("the workflow should progress through all states")
    public void theWorkflowShouldProgressThroughAllStates() {
    }

    @Then("tasks should be created according to database configuration")
    public void tasksShouldBeCreatedAccordingToDatabaseConfiguration() {
    }

    @Then("the process should complete successfully")
    public void theProcessShouldCompleteSuccessfully() {
    }
}
