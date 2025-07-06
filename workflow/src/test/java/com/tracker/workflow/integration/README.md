# Workflow Module Integration Tests

This directory contains integration tests for the tracker-workflow module. These tests verify that the workflow system works correctly with a real PostgreSQL database.

## Test Structure

The integration tests are organized as follows:

- `AbstractIntegrationTest.java`: Base class for all integration tests, providing common functionality and test data setup.
- `TestConfig.java`: Configuration class for the test environment, including database connection settings.
- `WorkflowServiceIntegrationTest.java`: Tests for the WorkflowService, including workflow state transitions and history tracking.
- `WorkflowTaskServiceIntegrationTest.java`: Tests for the WorkflowTaskService, including task creation, completion, delegation, and escalation.
- `WorkflowRuleServiceIntegrationTest.java`: Tests for the WorkflowRuleService, including rule evaluation for different business scenarios.

## Running the Tests

### Prerequisites

1. PostgreSQL database server running locally
2. A database named `tracker_test` created in PostgreSQL
3. PostgreSQL user `postgres` with password `postgres` having access to the `tracker_test` database

### Database Setup

Before running the tests, ensure you have created the test database:

```sql
CREATE DATABASE tracker_test;
```

### Running Tests

You can run the tests using Maven:

```bash
mvn test -Dtest=com.odyssey.workflow.integration.*
```

Or run individual test classes:

```bash
mvn test -Dtest=integration.com.tracker.workflow.WorkflowServiceIntegrationTest
```

## Test Configuration

The tests use the following configuration:

- Database: PostgreSQL (localhost:5432/tracker_test)
- Hibernate DDL: create-drop (tables are created and dropped for each test run)
- Flyway: Disabled for tests
- Transaction management: Each test method runs in a transaction that is rolled back after the test

## Adding New Tests

To add new integration tests:

1. Create a new test class that extends `AbstractIntegrationTest`
2. Use the `@Autowired` annotation to inject the services or repositories you need to test
3. Write test methods using JUnit 5 assertions
4. Use the `createTestProcessData` helper method to create test data

Example:

```java
@Test
public void testSomeWorkflowFeature() {
    // Arrange
    String userId = "test-user";
    Map<String, Object> processData = createTestProcessData(userId);
    
    // Act
    // ... perform the test actions
    
    // Assert
    // ... verify the results
}
```