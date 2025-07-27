package com.tracker.workflow.service;

import com.tracker.workflow.dto.TaskAssignmentConfig;
import java.util.HashMap;
import java.util.Map;
import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.UserRoleRepository;
import com.tracker.workflow.repository.WorkflowTaskAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowTaskAssignmentServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private WorkflowDefinitionService workflowDefinitionService;

    @Mock
    private WorkflowTaskAssignmentRepository assignmentRepository;

    @InjectMocks
    private WorkflowTaskAssignmentService assignmentService;

    private WorkflowDefinition workflowDefinition;
    private WorkflowStateDefinition stateDefinition;
    private WorkflowTaskAssignment taskAssignment;

    @BeforeEach
    void setUp() {
        workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setWorkflowName("test-workflow");

        stateDefinition = new WorkflowStateDefinition();
        stateDefinition.setStateName("PENDING_FINANCE_APPROVAL");
        stateDefinition.setId(1L);

        workflowDefinition.setStates(Arrays.asList(stateDefinition));

        taskAssignment = new WorkflowTaskAssignment();
        taskAssignment.setState(stateDefinition);
        taskAssignment.setAssignmentType(WorkflowTaskAssignment.AssignmentType.ROLE);
        Map<String, Object> assignmentConfig = new HashMap<>();
        assignmentConfig.put("assigneeValue", "finance-manager");
        taskAssignment.setAssignmentConfig(assignmentConfig);
        
        Map<String, Object> taskTemplate = new HashMap<>();
        taskTemplate.put("name", "Finance Review");
        taskAssignment.setTaskTemplate(taskTemplate);
        taskAssignment.setCompletionStrategy(CompletionStrategy.ALL_REQUIRED);
        
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
    }

    @Test
    void getAssignmentForState_ActiveWorkflowExists_ReturnsAssignment() {
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
        
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(workflowDefinition));
        when(assignmentRepository.findByStateId(1L))
                .thenReturn(Optional.of(taskAssignment));

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "PENDING_FINANCE_APPROVAL");

        assertNotNull(result);
        assertEquals("Finance Review", result.getTaskName());
        assertEquals("ALL_REQUIRED", result.getCompletionStrategy());
        assertEquals(WorkflowTaskAssignment.AssignmentType.ROLE, result.getAssignmentType());
    }

    @Test
    void getAssignmentForState_NoActiveWorkflow_ReturnsNull() {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.empty());

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "PENDING_FINANCE_APPROVAL");

        assertNull(result);
    }

    @Test
    void getAssignmentForState_StateNotFound_ReturnsNull() {
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
        
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(workflowDefinition));

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "NON_EXISTENT_STATE");

        assertNull(result);
    }

    @Test
    void resolveRoleBasedAssignees_ValidRole_ReturnsUsers() {
        UserRole userRole1 = new UserRole();
        userRole1.setUserId("user1");
        UserRole userRole2 = new UserRole();
        userRole2.setUserId("user2");

        when(userRoleRepository.findUserIdsByRoleName("finance-manager"))
                .thenReturn(Arrays.asList("user1", "user2"));

        List<String> result = assignmentService.resolveRoleBasedAssignees("finance-manager");

        assertEquals(2, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
    }

    @Test
    void resolveRoleBasedAssignees_NoUsersInRole_ReturnsEmptyList() {
        when(userRoleRepository.findUserIdsByRoleName("empty-role"))
                .thenReturn(Arrays.asList());

        List<String> result = assignmentService.resolveRoleBasedAssignees("empty-role");

        assertTrue(result.isEmpty());
    }

    @Test
    void resolveUserBasedAssignees_SingleUser_ReturnsList() {
        List<String> result = assignmentService.resolveUserBasedAssignees("user123");

        assertEquals(1, result.size());
        assertEquals("user123", result.get(0));
    }

    @Test
    void resolveUserBasedAssignees_MultipleUsers_ReturnsList() {
        List<String> result = assignmentService.resolveUserBasedAssignees("user1,user2,user3");

        assertEquals(3, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        assertTrue(result.contains("user3"));
    }

    @Test
    void resolveUserBasedAssignees_UsersWithSpaces_TrimsAndReturnsList() {
        List<String> result = assignmentService.resolveUserBasedAssignees("user1, user2 , user3");

        assertEquals(3, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        assertTrue(result.contains("user3"));
    }

    @Test
    void getAssignmentForState_RoleAssignment_ReturnsCorrectConfig() {
        taskAssignment.setAssignmentType(WorkflowTaskAssignment.AssignmentType.ROLE);
        Map<String, Object> roleConfig = new HashMap<>();
        roleConfig.put("assigneeValue", "finance-team");
        taskAssignment.setAssignmentConfig(roleConfig);
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
        
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(workflowDefinition));
        when(assignmentRepository.findByStateId(1L))
                .thenReturn(Optional.of(taskAssignment));

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "PENDING_FINANCE_APPROVAL");

        assertNotNull(result);
        assertEquals(WorkflowTaskAssignment.AssignmentType.ROLE, result.getAssignmentType());
    }

    @Test
    void getAssignmentForState_UserAssignment_ReturnsCorrectConfig() {
        taskAssignment.setAssignmentType(WorkflowTaskAssignment.AssignmentType.USER);
        Map<String, Object> userConfig = new HashMap<>();
        userConfig.put("assigneeValue", "manager123");
        taskAssignment.setAssignmentConfig(userConfig);
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
        
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(workflowDefinition));
        when(assignmentRepository.findByStateId(1L))
                .thenReturn(Optional.of(taskAssignment));

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "PENDING_FINANCE_APPROVAL");

        assertNotNull(result);
        assertEquals(WorkflowTaskAssignment.AssignmentType.USER, result.getAssignmentType());
    }

    @Test
    void getAssignmentForState_DynamicAssignment_ReturnsCorrectConfig() {
        taskAssignment.setAssignmentType(WorkflowTaskAssignment.AssignmentType.DYNAMIC);
        Map<String, Object> dynamicConfig = new HashMap<>();
        dynamicConfig.put("assigneeValue", "{\"expression\": \"processData.department + '-manager'\"}");
        taskAssignment.setAssignmentConfig(dynamicConfig);
        workflowDefinition.setTaskAssignments(Arrays.asList(taskAssignment));
        
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(workflowDefinition));
        when(assignmentRepository.findByStateId(1L))
                .thenReturn(Optional.of(taskAssignment));

        TaskAssignmentConfig result = assignmentService.getAssignmentForState("process123", "PENDING_FINANCE_APPROVAL");

        assertNotNull(result);
        assertEquals(WorkflowTaskAssignment.AssignmentType.DYNAMIC, result.getAssignmentType());
    }
}
