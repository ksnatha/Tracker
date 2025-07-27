package com.tracker.workflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class DynamicWorkflowGuardFactory {
    
    private final WorkflowExpressionEvaluator expressionEvaluator;
    
    public Guard<String, String> createGuard(String guardExpression) {
        return context -> {
            try {
                Map<String, Object> processData = getProcessData(context);
                Map<String, Object> contextData = getContextData(context);
                
                return expressionEvaluator.evaluate(guardExpression, processData, contextData);
            } catch (Exception e) {
                log.error("Error evaluating guard expression: {}", guardExpression, e);
                return false;
            }
        };
    }
    
    private Map<String, Object> getProcessData(StateContext<String, String> context) {
        Object processData = context.getExtendedState().getVariables().get("processData");
        if (processData instanceof Map) {
            return (Map<String, Object>) processData;
        }
        return new HashMap<>();
    }
    
    private Map<String, Object> getContextData(StateContext<String, String> context) {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("currentState", context.getSource() != null ? context.getSource().getId() : null);
        contextData.put("targetState", context.getTarget() != null ? context.getTarget().getId() : null);
        contextData.put("event", context.getEvent());
        return contextData;
    }
}
