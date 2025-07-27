package com.tracker.workflow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowBootstrapServiceTest {

    @Mock
    private WorkflowDefinitionService workflowDefinitionService;

    @Mock
    private WorkflowMigrationService migrationService;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private WorkflowBootstrapService bootstrapService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void run_NoActiveWorkflow_PerformsMigration() throws Exception {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.empty());

        bootstrapService.run(applicationArguments);

        verify(migrationService).migrateHardcodedWorkflowToDatabase();
    }

    @Test
    void run_ActiveWorkflowExists_SkipsMigration() throws Exception {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.of(mock(com.tracker.workflow.model.WorkflowDefinition.class)));

        bootstrapService.run(applicationArguments);

        verify(migrationService, never()).migrateHardcodedWorkflowToDatabase();
    }

    @Test
    void run_MigrationServiceThrowsException_PropagatesException() {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.empty());
        doThrow(new RuntimeException("Migration failed")).when(migrationService)
                .migrateHardcodedWorkflowToDatabase();

        assertThrows(RuntimeException.class, () -> 
                bootstrapService.run(applicationArguments));
    }

    @Test
    void run_WorkflowDefinitionServiceThrowsException_PropagatesException() {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> 
                bootstrapService.run(applicationArguments));
    }

    @Test
    void run_NullApplicationArguments_HandlesGracefully() throws Exception {
        when(workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> bootstrapService.run(null));
        verify(migrationService).migrateHardcodedWorkflowToDatabase();
    }
}
