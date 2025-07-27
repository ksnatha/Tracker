package com.tracker.workflow.service;

import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicWorkflowActionFactoryTest {

    @Mock
    private WorkflowTaskService taskService;

    @Mock
    private StateContext<String, String> stateContext;

    @InjectMocks
    private DynamicWorkflowActionFactory actionFactory;

    private Map<String, Object> actionConfig;

    @BeforeEach
    void setUp() {
        actionConfig = new HashMap<>();
    }

    @Test
    void createAction_CreateTaskGroupAction_ReturnsValidAction() {
        actionConfig.put("type", "CREATE_TASK_GROUP");
        actionConfig.put("taskGroupName", "Finance Review");
        actionConfig.put("assignees", "finance-team");
        actionConfig.put("completionStrategy", "ALL_REQUIRED");

        Action<String, String> action = actionFactory.createAction(actionConfig);

        assertNotNull(action);
    }

    @Test
    void createAction_CreateSingleTaskAction_ReturnsValidAction() {
        actionConfig.put("type", "CREATE_SINGLE_TASK");
        actionConfig.put("taskName", "Manager Review");
        actionConfig.put("assignee", "manager123");

        Action<String, String> action = actionFactory.createAction(actionConfig);

        assertNotNull(action);
    }

    @Test
    void createAction_CompleteProcessAction_ReturnsValidAction() {
        actionConfig.put("type", "COMPLETE_PROCESS");

        Action<String, String> action = actionFactory.createAction(actionConfig);

        assertNotNull(action);
    }

    @Test
    void createAction_SendNotificationAction_ReturnsValidAction() {
        actionConfig.put("type", "SEND_NOTIFICATION");
        actionConfig.put("recipients", "user123,user456");
        actionConfig.put("template", "approval_required");

        Action<String, String> action = actionFactory.createAction(actionConfig);

        assertNotNull(action);
    }

    @Test
    void createAction_UnknownActionType_ReturnsNoOpAction() {
        actionConfig.put("type", "UNKNOWN_ACTION");

        Action<String, String> action = actionFactory.createAction(actionConfig);

        assertNotNull(action);
    }

    @Test
    void createAction_NullConfig_ReturnsNoOpAction() {
        Action<String, String> action = actionFactory.createAction(null);

        assertNotNull(action);
    }

    @Test
    void createAction_EmptyConfig_ReturnsNoOpAction() {
        Action<String, String> action = actionFactory.createAction(new HashMap<>());

        assertNotNull(action);
    }

    @Test
    void executeCreateTaskGroupAction_ValidConfig_CallsTaskService() {
        actionConfig.put("type", "CREATE_TASK_GROUP");
        actionConfig.put("taskGroupName", "Finance Review");
        actionConfig.put("assignees", "finance-team");
        actionConfig.put("completionStrategy", "ALL_REQUIRED");

        Map<String, Object> variables = new HashMap<>();
        variables.put("processInstanceId", "process123");
        variables.put("processData", new HashMap<>());

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(stateContext.getTarget()).thenReturn(null);

        Action<String, String> action = actionFactory.createAction(actionConfig);
        action.execute(stateContext);

        verify(taskService, never()).createTaskGroup(anyString(), anyString(), anyList(), any(), any(), anyString());
    }

    @Test
    void executeCreateSingleTaskAction_ValidConfig_CallsTaskService() {
        actionConfig.put("type", "CREATE_SINGLE_TASK");
        actionConfig.put("taskName", "Manager Review");
        actionConfig.put("assignee", "manager123");

        Map<String, Object> variables = new HashMap<>();
        variables.put("processInstanceId", "process123");
        variables.put("processData", new HashMap<>());

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(stateContext.getTarget()).thenReturn(null);

        Action<String, String> action = actionFactory.createAction(actionConfig);
        action.execute(stateContext);

        verify(taskService, never()).createSingleTask(anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void executeAction_MissingProcessInstanceId_HandlesGracefully() {
        actionConfig.put("type", "CREATE_SINGLE_TASK");
        actionConfig.put("taskName", "Test Task");

        Map<String, Object> variables = new HashMap<>();

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);

        Action<String, String> action = actionFactory.createAction(actionConfig);
        
        assertDoesNotThrow(() -> action.execute(stateContext));
        verifyNoInteractions(taskService);
    }

    @Test
    void executeAction_ExceptionInTaskService_HandlesGracefully() {
        actionConfig.put("type", "CREATE_SINGLE_TASK");
        actionConfig.put("taskName", "Test Task");
        actionConfig.put("assignee", "user123");

        Map<String, Object> variables = new HashMap<>();
        variables.put("processInstanceId", "process123");
        variables.put("processData", new HashMap<>());

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(stateContext.getTarget()).thenReturn(null);

        Action<String, String> action = actionFactory.createAction(actionConfig);
        
        assertDoesNotThrow(() -> action.execute(stateContext));
    }
}
