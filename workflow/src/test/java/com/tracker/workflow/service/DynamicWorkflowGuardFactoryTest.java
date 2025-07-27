package com.tracker.workflow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicWorkflowGuardFactoryTest {

    @Mock
    private WorkflowExpressionEvaluator expressionEvaluator;

    @Mock
    private StateContext<String, String> stateContext;

    @InjectMocks
    private DynamicWorkflowGuardFactory guardFactory;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createGuard_ValidExpression_ReturnsGuard() {
        String expression = "{\"amount\": {\"$gte\": 1000}}";

        Guard<String, String> guard = guardFactory.createGuard(expression);

        assertNotNull(guard);
    }

    @Test
    void createGuard_NullExpression_ReturnsAlwaysTrueGuard() {
        Guard<String, String> guard = guardFactory.createGuard(null);

        assertNotNull(guard);
        assertTrue(guard.evaluate(stateContext));
    }

    @Test
    void createGuard_EmptyExpression_ReturnsAlwaysTrueGuard() {
        Guard<String, String> guard = guardFactory.createGuard("");

        assertNotNull(guard);
        assertTrue(guard.evaluate(stateContext));
    }

    @Test
    void evaluateGuard_ValidExpressionAndData_ReturnsTrue() {
        String expression = "{\"amount\": {\"$gte\": 1000}}";
        
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 1500.0);
        variables.put("processData", processData);

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(expressionEvaluator.evaluate(eq(expression), eq(processData), any(Map.class)))
                .thenReturn(true);

        Guard<String, String> guard = guardFactory.createGuard(expression);
        boolean result = guard.evaluate(stateContext);

        assertTrue(result);
        verify(expressionEvaluator).evaluate(eq(expression), eq(processData), any(Map.class));
    }

    @Test
    void evaluateGuard_ValidExpressionAndData_ReturnsFalse() {
        String expression = "{\"amount\": {\"$gte\": 1000}}";
        
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 500.0);
        variables.put("processData", processData);

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(expressionEvaluator.evaluate(eq(expression), eq(processData), any(Map.class)))
                .thenReturn(false);

        Guard<String, String> guard = guardFactory.createGuard(expression);
        boolean result = guard.evaluate(stateContext);

        assertFalse(result);
        verify(expressionEvaluator).evaluate(eq(expression), eq(processData), any(Map.class));
    }

    @Test
    void evaluateGuard_MissingProcessData_ReturnsFalse() {
        String expression = "{\"amount\": {\"$gte\": 1000}}";
        
        Map<String, Object> variables = new HashMap<>();

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);

        Guard<String, String> guard = guardFactory.createGuard(expression);
        boolean result = guard.evaluate(stateContext);

        assertFalse(result);
        verifyNoInteractions(expressionEvaluator);
    }

    @Test
    void evaluateGuard_ExceptionInEvaluator_ReturnsFalse() {
        String expression = "{\"amount\": {\"$gte\": 1000}}";
        
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 1500.0);
        variables.put("processData", processData);

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(expressionEvaluator.evaluate(eq(expression), eq(processData), any(Map.class)))
                .thenThrow(new RuntimeException("Evaluation error"));

        Guard<String, String> guard = guardFactory.createGuard(expression);
        boolean result = guard.evaluate(stateContext);

        assertFalse(result);
    }

    @Test
    void evaluateGuard_ComplexExpression_EvaluatesCorrectly() {
        String expression = "{\"$and\": [{\"amount\": {\"$gte\": 1000}}, {\"department\": {\"$eq\": \"finance\"}}]}";
        
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 1500.0);
        processData.put("department", "finance");
        variables.put("processData", processData);

        when(stateContext.getExtendedState()).thenReturn(mock(org.springframework.statemachine.ExtendedState.class));
        when(stateContext.getExtendedState().getVariables()).thenReturn((Map<Object, Object>) (Map<?, ?>) variables);
        when(expressionEvaluator.evaluate(eq(expression), eq(processData), any(Map.class)))
                .thenReturn(true);

        Guard<String, String> guard = guardFactory.createGuard(expression);
        boolean result = guard.evaluate(stateContext);

        assertTrue(result);
        verify(expressionEvaluator).evaluate(eq(expression), eq(processData), any(Map.class));
    }
}
