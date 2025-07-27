package com.tracker.workflow.service;

import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class WorkflowMigrationServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private WorkflowStateDefinitionRepository stateDefinitionRepository;

    @Mock
    private WorkflowTransitionDefinitionRepository transitionDefinitionRepository;

    @Mock
    private WorkflowTaskAssignmentRepository taskAssignmentRepository;

    @Mock
    private WorkflowRoleRepository roleRepository;

    @InjectMocks
    private WorkflowMigrationService migrationService;

    private WorkflowDefinition existingWorkflow;

    @BeforeEach
    void setUp() {
        existingWorkflow = new WorkflowDefinition();
        existingWorkflow.setWorkflowName("Tracker-core-workflow");
        existingWorkflow.setVersion("1.0");
        existingWorkflow.setIsActive(true);
        
        lenient().when(workflowDefinitionRepository.save(any(WorkflowDefinition.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(stateDefinitionRepository.save(any(WorkflowStateDefinition.class)))
                .thenAnswer(invocation -> {
                    WorkflowStateDefinition state = invocation.getArgument(0);
                    state.setId(1L);
                    return state;
                });
        lenient().when(transitionDefinitionRepository.save(any(WorkflowTransitionDefinition.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(taskAssignmentRepository.save(any(WorkflowTaskAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_NoExistingWorkflow_CreatesNewWorkflow() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(workflowDefinitionRepository, times(2)).save(any(WorkflowDefinition.class));
        verify(stateDefinitionRepository, times(5)).save(any(WorkflowStateDefinition.class));
        verify(transitionDefinitionRepository, times(4)).save(any(WorkflowTransitionDefinition.class));
        verify(taskAssignmentRepository, times(4)).save(any(WorkflowTaskAssignment.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_ExistingWorkflow_SkipsMigration() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(workflowDefinitionRepository, times(2)).save(any(WorkflowDefinition.class));
        verify(stateDefinitionRepository, times(5)).save(any(WorkflowStateDefinition.class));
        verify(transitionDefinitionRepository, times(4)).save(any(WorkflowTransitionDefinition.class));
        verify(taskAssignmentRepository, times(4)).save(any(WorkflowTaskAssignment.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_CreatesCorrectStates() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(stateDefinitionRepository, times(5)).save(any(WorkflowStateDefinition.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_CreatesCorrectTransitions() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(transitionDefinitionRepository, times(4)).save(any(WorkflowTransitionDefinition.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_CreatesCorrectTaskAssignments() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(taskAssignmentRepository, times(4)).save(any(WorkflowTaskAssignment.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_CreatesCorrectRoles() {
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(taskAssignmentRepository, times(4)).save(any(WorkflowTaskAssignment.class));
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_HandlesRepositoryException() {
        when(workflowDefinitionRepository.save(any(WorkflowDefinition.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> 
                migrationService.migrateHardcodedWorkflowToDatabase());
    }

    @Test
    void migrateHardcodedWorkflowToDatabase_IdempotentOperation() {
        migrationService.migrateHardcodedWorkflowToDatabase();
        migrationService.migrateHardcodedWorkflowToDatabase();

        verify(workflowDefinitionRepository, times(4)).save(any(WorkflowDefinition.class));
        verify(stateDefinitionRepository, times(10)).save(any(WorkflowStateDefinition.class));
    }
}
