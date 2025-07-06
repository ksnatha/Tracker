package com.tracker.workflow.integration;

import com.tracker.workflow.service.WorkflowRuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the WorkflowRuleService.
 * These tests verify that the business rules are evaluated correctly.
 */
@ExtendWith(SpringExtension.class)
public class WorkflowRuleServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorkflowRuleService ruleService;

    /**
     * Test the finance approval rule.
     * Verifies that finance approval is required for amounts >= 500.
     */
    @Test
    public void testFinanceApprovalRule() {
        // Test case 1: Amount below threshold (no finance approval required)
        Map<String, Object> processData1 = new HashMap<>();
        processData1.put("amount", 499.99);
        
        boolean result1 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData1);
        assertFalse(result1, "Finance approval should not be required for amounts < 500");
        
        // Test case 2: Amount at threshold (finance approval required)
        Map<String, Object> processData2 = new HashMap<>();
        processData2.put("amount", 500.0);
        
        boolean result2 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData2);
        assertTrue(result2, "Finance approval should be required for amounts >= 500");
        
        // Test case 3: Amount above threshold (finance approval required)
        Map<String, Object> processData3 = new HashMap<>();
        processData3.put("amount", 1000.0);
        
        boolean result3 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData3);
        assertTrue(result3, "Finance approval should be required for amounts >= 500");
        
        // Test case 4: No amount specified (default to requiring approval)
        Map<String, Object> processData4 = new HashMap<>();
        
        boolean result4 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData4);
        assertTrue(result4, "Finance approval should be required by default if amount not specified");
    }

    /**
     * Test the CEO approval rule.
     * Verifies that CEO approval is required for amounts >= 10000.
     */
    @Test
    public void testCeoApprovalRule() {
        // Test case 1: Amount below threshold (no CEO approval required)
        Map<String, Object> processData1 = new HashMap<>();
        processData1.put("amount", 9999.99);
        
        boolean result1 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData1);
        assertFalse(result1, "CEO approval should not be required for amounts < 10000");
        
        // Test case 2: Amount at threshold (CEO approval required)
        Map<String, Object> processData2 = new HashMap<>();
        processData2.put("amount", 10000.0);
        
        boolean result2 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData2);
        assertTrue(result2, "CEO approval should be required for amounts >= 10000");
        
        // Test case 3: Amount above threshold (CEO approval required)
        Map<String, Object> processData3 = new HashMap<>();
        processData3.put("amount", 15000.0);
        
        boolean result3 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData3);
        assertTrue(result3, "CEO approval should be required for amounts >= 10000");
        
        // Test case 4: No amount specified (default to not requiring approval)
        Map<String, Object> processData4 = new HashMap<>();
        
        boolean result4 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData4);
        assertFalse(result4, "CEO approval should not be required by default if amount not specified");
    }

    /**
     * Test an unknown rule.
     * Verifies that unknown rules return false.
     */
    @Test
    public void testUnknownRule() {
        Map<String, Object> processData = new HashMap<>();
        processData.put("amount", 1000.0);
        
        boolean result = ruleService.evaluateRule("UNKNOWN_RULE", processData);
        assertFalse(result, "Unknown rules should return false");
    }

    /**
     * Test rule evaluation with non-numeric values.
     * Verifies that rules handle non-numeric values gracefully.
     */
    @Test
    public void testRuleWithNonNumericValues() {
        // Test with string value
        Map<String, Object> processData1 = new HashMap<>();
        processData1.put("amount", "not a number");
        
        boolean result1 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData1);
        assertTrue(result1, "Finance approval should default to true for non-numeric values");
        
        boolean result2 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData1);
        assertFalse(result2, "CEO approval should default to false for non-numeric values");
        
        // Test with null value
        Map<String, Object> processData2 = new HashMap<>();
        processData2.put("amount", null);
        
        boolean result3 = ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData2);
        assertTrue(result3, "Finance approval should default to true for null values");
        
        boolean result4 = ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData2);
        assertFalse(result4, "CEO approval should default to false for null values");
    }
}