Feature: Database-Driven Workflow Management
  As a workflow administrator
  I want to manage workflows through database configuration
  So that I can modify workflows without code changes

  Background:
    Given the workflow system is initialized
    And the database is clean

  Scenario: Create and activate new workflow version
    Given I have a workflow definition "expense-approval" version "1.0"
    When I create a new version "2.0" with description "Updated approval process"
    And I activate version "2.0"
    Then the workflow "expense-approval" should have active version "2.0"
    And version "1.0" should be inactive

  Scenario: Assign tasks based on user roles
    Given I have an active workflow with role-based assignments
    And user "john.doe" has role "finance-manager"
    And user "jane.smith" has role "finance-manager"
    When a workflow reaches state "PENDING_FINANCE_APPROVAL"
    Then tasks should be assigned to users with role "finance-manager"
    And the assignees should include "john.doe" and "jane.smith"

  Scenario: Evaluate JSON expressions for workflow guards
    Given I have a workflow with guard expression '{"amount": {"$gte": 1000}}'
    And the process data contains amount 1500
    When the workflow evaluates the guard condition
    Then the guard should return true
    And the transition should be allowed

  Scenario: Evaluate JSON expressions with false condition
    Given I have a workflow with guard expression '{"amount": {"$gte": 1000}}'
    And the process data contains amount 500
    When the workflow evaluates the guard condition
    Then the guard should return false
    And the transition should be blocked

  Scenario: Migrate hardcoded workflow to database format
    Given there is no active workflow in the database
    When the migration service runs
    Then a new workflow definition should be created
    And it should have 5 states
    And it should have 4 transitions
    And it should have role-based task assignments

  Scenario: Execute complete workflow with database-driven configuration
    Given I have a database-driven workflow configuration
    And the workflow has dynamic task assignments
    When I start a new workflow process
    And I trigger workflow events in sequence
    Then the workflow should progress through all states
    And tasks should be created according to database configuration
    And the process should complete successfully
