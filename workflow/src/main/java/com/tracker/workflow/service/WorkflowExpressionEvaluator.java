package com.tracker.workflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WorkflowExpressionEvaluator {
    
    private final ObjectMapper objectMapper;
    
    public boolean evaluate(String expression, Map<String, Object> processData, Map<String, Object> context) {
        if (expression == null || expression.trim().isEmpty()) {
            return true;
        }
        
        try {
            JsonNode expressionNode = objectMapper.readTree(expression);
            return evaluateNode(expressionNode, processData, context);
        } catch (Exception e) {
            log.error("Error evaluating expression: {}", expression, e);
            return false;
        }
    }
    
    private boolean evaluateNode(JsonNode node, Map<String, Object> processData, Map<String, Object> context) {
        if (node.isObject()) {
            return evaluateObjectNode(node, processData, context);
        }
        return false;
    }
    
    private boolean evaluateObjectNode(JsonNode node, Map<String, Object> processData, Map<String, Object> context) {
        if (node.has("$and")) {
            return evaluateAndOperator(node.get("$and"), processData, context);
        }
        if (node.has("$or")) {
            return evaluateOrOperator(node.get("$or"), processData, context);
        }
        if (node.has("$not")) {
            return !evaluateNode(node.get("$not"), processData, context);
        }
        
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            
            if (!evaluateFieldCondition(fieldName, fieldValue, processData, context)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean evaluateAndOperator(JsonNode andNode, Map<String, Object> processData, Map<String, Object> context) {
        if (andNode.isArray()) {
            for (JsonNode condition : andNode) {
                if (!evaluateNode(condition, processData, context)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean evaluateOrOperator(JsonNode orNode, Map<String, Object> processData, Map<String, Object> context) {
        if (orNode.isArray()) {
            for (JsonNode condition : orNode) {
                if (evaluateNode(condition, processData, context)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    private boolean evaluateFieldCondition(String fieldName, JsonNode condition, Map<String, Object> processData, Map<String, Object> context) {
        Object fieldValue = getFieldValue(fieldName, processData, context);
        
        if (condition.isObject()) {
            return evaluateComparisonOperators(fieldValue, condition);
        } else {
            return compareValues(fieldValue, condition);
        }
    }
    
    private Object getFieldValue(String fieldName, Map<String, Object> processData, Map<String, Object> context) {
        if (processData.containsKey(fieldName)) {
            return processData.get(fieldName);
        }
        
        if (context.containsKey(fieldName)) {
            return context.get(fieldName);
        }
        
        return null;
    }
    
    private boolean evaluateComparisonOperators(Object fieldValue, JsonNode condition) {
        var operators = condition.fields();
        while (operators.hasNext()) {
            var operator = operators.next();
            String op = operator.getKey();
            JsonNode value = operator.getValue();
            
            switch (op) {
                case "$eq":
                    if (!compareValues(fieldValue, value)) return false;
                    break;
                case "$ne":
                    if (compareValues(fieldValue, value)) return false;
                    break;
                case "$gt":
                    if (!compareGreaterThan(fieldValue, value)) return false;
                    break;
                case "$gte":
                    if (!compareGreaterThanOrEqual(fieldValue, value)) return false;
                    break;
                case "$lt":
                    if (!compareLessThan(fieldValue, value)) return false;
                    break;
                case "$lte":
                    if (!compareLessThanOrEqual(fieldValue, value)) return false;
                    break;
                case "$in":
                    if (!compareIn(fieldValue, value)) return false;
                    break;
                case "$nin":
                    if (compareIn(fieldValue, value)) return false;
                    break;
                default:
                    log.warn("Unknown operator: {}", op);
                    return false;
            }
        }
        return true;
    }
    
    private boolean compareValues(Object fieldValue, JsonNode expectedValue) {
        if (fieldValue == null) {
            return expectedValue.isNull();
        }
        
        if (expectedValue.isTextual()) {
            return fieldValue.toString().equals(expectedValue.asText());
        } else if (expectedValue.isNumber()) {
            if (fieldValue instanceof Number) {
                return ((Number) fieldValue).doubleValue() == expectedValue.asDouble();
            }
        } else if (expectedValue.isBoolean()) {
            if (fieldValue instanceof Boolean) {
                return fieldValue.equals(expectedValue.asBoolean());
            }
        }
        
        return false;
    }
    
    private boolean compareGreaterThan(Object fieldValue, JsonNode value) {
        if (fieldValue instanceof Number && value.isNumber()) {
            return ((Number) fieldValue).doubleValue() > value.asDouble();
        }
        return false;
    }
    
    private boolean compareGreaterThanOrEqual(Object fieldValue, JsonNode value) {
        if (fieldValue instanceof Number && value.isNumber()) {
            return ((Number) fieldValue).doubleValue() >= value.asDouble();
        }
        return false;
    }
    
    private boolean compareLessThan(Object fieldValue, JsonNode value) {
        if (fieldValue instanceof Number && value.isNumber()) {
            return ((Number) fieldValue).doubleValue() < value.asDouble();
        }
        return false;
    }
    
    private boolean compareLessThanOrEqual(Object fieldValue, JsonNode value) {
        if (fieldValue instanceof Number && value.isNumber()) {
            return ((Number) fieldValue).doubleValue() <= value.asDouble();
        }
        return false;
    }
    
    private boolean compareIn(Object fieldValue, JsonNode arrayValue) {
        if (arrayValue.isArray()) {
            for (JsonNode item : arrayValue) {
                if (compareValues(fieldValue, item)) {
                    return true;
                }
            }
        }
        return false;
    }
}
