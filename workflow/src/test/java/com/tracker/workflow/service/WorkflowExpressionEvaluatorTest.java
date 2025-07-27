package com.tracker.workflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowExpressionEvaluatorTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WorkflowExpressionEvaluator evaluator;

    private Map<String, Object> processData;
    private Map<String, Object> context;

    @BeforeEach
    void setUp() {
        evaluator = new WorkflowExpressionEvaluator(new ObjectMapper());
        
        processData = new HashMap<>();
        processData.put("amount", 1000.0);
        processData.put("department", "engineering");
        processData.put("priority", "HIGH");
        processData.put("approved", true);

        context = new HashMap<>();
        context.put("userId", "user123");
        context.put("role", "manager");
    }

    @Test
    void evaluate_NullExpression_ReturnsTrue() {
        boolean result = evaluator.evaluate(null, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_EmptyExpression_ReturnsTrue() {
        boolean result = evaluator.evaluate("", processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_SimpleEqualityExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$eq\": 1000.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_SimpleEqualityExpression_ReturnsFalse() {
        String expression = "{\"amount\": {\"$eq\": 500.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_NotEqualExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$ne\": 500.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_GreaterThanExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$gt\": 500.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_GreaterThanExpression_ReturnsFalse() {
        String expression = "{\"amount\": {\"$gt\": 1500.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_GreaterThanOrEqualExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$gte\": 1000.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_LessThanExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$lt\": 1500.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_LessThanOrEqualExpression_ReturnsTrue() {
        String expression = "{\"amount\": {\"$lte\": 1000.0}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_InExpression_ReturnsTrue() {
        String expression = "{\"department\": {\"$in\": [\"engineering\", \"finance\"]}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_InExpression_ReturnsFalse() {
        String expression = "{\"department\": {\"$in\": [\"marketing\", \"sales\"]}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_NotInExpression_ReturnsTrue() {
        String expression = "{\"department\": {\"$nin\": [\"marketing\", \"sales\"]}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_AndExpression_ReturnsTrue() {
        String expression = "{\"$and\": [{\"amount\": {\"$gte\": 500}}, {\"priority\": {\"$eq\": \"HIGH\"}}]}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_AndExpression_ReturnsFalse() {
        String expression = "{\"$and\": [{\"amount\": {\"$gte\": 1500}}, {\"priority\": {\"$eq\": \"HIGH\"}}]}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_OrExpression_ReturnsTrue() {
        String expression = "{\"$or\": [{\"amount\": {\"$gte\": 1500}}, {\"priority\": {\"$eq\": \"HIGH\"}}]}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_OrExpression_ReturnsFalse() {
        String expression = "{\"$or\": [{\"amount\": {\"$gte\": 1500}}, {\"priority\": {\"$eq\": \"LOW\"}}]}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_NotExpression_ReturnsTrue() {
        String expression = "{\"$not\": {\"amount\": {\"$eq\": 500}}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_NotExpression_ReturnsFalse() {
        String expression = "{\"$not\": {\"amount\": {\"$eq\": 1000}}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_BooleanExpression_ReturnsTrue() {
        String expression = "{\"approved\": {\"$eq\": true}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_ContextFieldExpression_ReturnsTrue() {
        String expression = "{\"role\": {\"$eq\": \"manager\"}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_ComplexNestedExpression_ReturnsTrue() {
        String expression = "{\"$and\": [{\"$or\": [{\"amount\": {\"$gte\": 1000}}, {\"priority\": {\"$eq\": \"URGENT\"}}]}, {\"department\": {\"$in\": [\"engineering\", \"finance\"]}}]}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_InvalidJsonExpression_ReturnsFalse() {
        String expression = "{invalid json}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_UnknownOperator_ReturnsFalse() {
        String expression = "{\"amount\": {\"$unknown\": 1000}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }

    @Test
    void evaluate_NullFieldValue_WithNullCheck_ReturnsTrue() {
        processData.put("nullField", null);
        String expression = "{\"nullField\": null}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertTrue(result);
    }

    @Test
    void evaluate_MissingField_ReturnsFalse() {
        String expression = "{\"missingField\": {\"$eq\": \"value\"}}";
        boolean result = evaluator.evaluate(expression, processData, context);
        assertFalse(result);
    }
}
