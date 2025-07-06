package com.tracker.workflow.service;

import com.tracker.workflow.repository.WorkflowRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowRuleService {

    private final WorkflowRuleRepository ruleRepository;

    public boolean evaluateRule(String ruleName, Map<String, Object> processData) {
        switch (ruleName) {
            case "FINANCE_APPROVAL_REQUIRED":
                return evaluateFinanceApprovalRequired(processData);
            case "CEO_APPROVAL_REQUIRED":
                return evaluateCeoApprovalRequired(processData);
            default:
                return false;
        }
    }

    private boolean evaluateFinanceApprovalRequired(Map<String, Object> processData) {
        Object amountObj = processData.get("amount");
        if (amountObj instanceof Number) {
            double amount = ((Number) amountObj).doubleValue();
            return amount >= 500.0;
        }
        return true; // Default to requiring approval if amount not specified
    }

    private boolean evaluateCeoApprovalRequired(Map<String, Object> processData) {
        Object amountObj = processData.get("amount");
        if (amountObj instanceof Number) {
            double amount = ((Number) amountObj).doubleValue();
            return amount >= 10000.0;
        }
        return false;
    }
}
