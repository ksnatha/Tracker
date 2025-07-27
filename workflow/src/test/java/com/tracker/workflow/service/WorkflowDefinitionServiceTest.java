package com.tracker.workflow.service;

import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.repository.WorkflowDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowDefinitionServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @InjectMocks
    private WorkflowDefinitionService workflowDefinitionService;

    private WorkflowDefinition testWorkflow;

    @BeforeEach
    void setUp() {
        testWorkflow = new WorkflowDefinition();
        testWorkflow.setId(1L);
        testWorkflow.setWorkflowName("test-workflow");
        testWorkflow.setVersion("1.0");
        testWorkflow.setDescription("Test workflow");
        testWorkflow.setIsActive(true);
        testWorkflow.setCreatedBy("test-user");
        testWorkflow.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getActiveWorkflow_ExistingWorkflow_ReturnsWorkflow() {
        when(workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue("test-workflow"))
                .thenReturn(Optional.of(testWorkflow));

        Optional<WorkflowDefinition> result = workflowDefinitionService.getActiveWorkflow("test-workflow");

        assertTrue(result.isPresent());
        assertEquals("test-workflow", result.get().getWorkflowName());
        assertEquals("1.0", result.get().getVersion());
        assertTrue(result.get().getIsActive());
    }

    @Test
    void getActiveWorkflow_NonExistentWorkflow_ReturnsEmpty() {
        when(workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue("non-existent"))
                .thenReturn(Optional.empty());

        Optional<WorkflowDefinition> result = workflowDefinitionService.getActiveWorkflow("non-existent");

        assertFalse(result.isPresent());
    }

    @Test
    void getWorkflowVersion_ExistingVersion_ReturnsWorkflow() {
        when(workflowDefinitionRepository.findByWorkflowNameAndVersion("test-workflow", "1.0"))
                .thenReturn(Optional.of(testWorkflow));

        Optional<WorkflowDefinition> result = workflowDefinitionService.getWorkflowVersion("test-workflow", "1.0");

        assertTrue(result.isPresent());
        assertEquals("1.0", result.get().getVersion());
    }

    @Test
    void getWorkflowVersion_NonExistentVersion_ReturnsEmpty() {
        when(workflowDefinitionRepository.findByWorkflowNameAndVersion("test-workflow", "2.0"))
                .thenReturn(Optional.empty());

        Optional<WorkflowDefinition> result = workflowDefinitionService.getWorkflowVersion("test-workflow", "2.0");

        assertFalse(result.isPresent());
    }

    @Test
    void getVersionHistory_ExistingWorkflow_ReturnsVersions() {
        WorkflowDefinition version2 = new WorkflowDefinition();
        version2.setWorkflowName("test-workflow");
        version2.setVersion("2.0");
        version2.setCreatedDate(LocalDateTime.now().plusDays(1));

        List<WorkflowDefinition> versions = Arrays.asList(version2, testWorkflow);
        when(workflowDefinitionRepository.findByWorkflowNameOrderByCreatedDateDesc("test-workflow"))
                .thenReturn(versions);

        List<WorkflowDefinition> result = workflowDefinitionService.getVersionHistory("test-workflow");

        assertEquals(2, result.size());
        assertEquals("2.0", result.get(0).getVersion());
        assertEquals("1.0", result.get(1).getVersion());
    }

    @Test
    void createWorkflowDefinition_ValidData_CreatesWorkflow() {
        when(workflowDefinitionRepository.save(any(WorkflowDefinition.class)))
                .thenReturn(testWorkflow);

        WorkflowDefinition result = workflowDefinitionService.createWorkflowDefinition(
                "test-workflow", "1.0", "Test workflow", "test-user");

        assertNotNull(result);
        verify(workflowDefinitionRepository).save(any(WorkflowDefinition.class));
    }

    @Test
    void createNewVersion_ExistingWorkflow_CreatesNewVersion() {
        WorkflowDefinition newVersion = new WorkflowDefinition();
        newVersion.setWorkflowName("test-workflow");
        newVersion.setVersion("2.0");
        newVersion.setIsActive(false);

        when(workflowDefinitionRepository.findByWorkflowNameAndVersion("test-workflow", "1.0"))
                .thenReturn(Optional.of(testWorkflow));
        when(workflowDefinitionRepository.save(any(WorkflowDefinition.class)))
                .thenReturn(newVersion);

        WorkflowDefinition result = workflowDefinitionService.createNewVersion(
                "test-workflow", "2.0", "1.0", "test-user");

        assertNotNull(result);
        assertEquals("2.0", result.getVersion());
        assertFalse(result.getIsActive());
        verify(workflowDefinitionRepository).save(any(WorkflowDefinition.class));
    }

    @Test
    void activateVersion_ExistingVersion_ActivatesVersion() {
        WorkflowDefinition inactiveVersion = new WorkflowDefinition();
        inactiveVersion.setId(2L);
        inactiveVersion.setWorkflowName("test-workflow");
        inactiveVersion.setVersion("2.0");
        inactiveVersion.setIsActive(false);

        when(workflowDefinitionRepository.findByWorkflowNameAndVersion("test-workflow", "2.0"))
                .thenReturn(Optional.of(inactiveVersion));
        when(workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue("test-workflow"))
                .thenReturn(Optional.of(testWorkflow));

        workflowDefinitionService.activateVersion("test-workflow", "2.0", "test-user");

        verify(workflowDefinitionRepository).save(testWorkflow);
        verify(workflowDefinitionRepository).save(inactiveVersion);
        assertFalse(testWorkflow.getIsActive());
        assertTrue(inactiveVersion.getIsActive());
    }

    @Test
    void activateVersion_NonExistentVersion_ThrowsException() {
        when(workflowDefinitionRepository.findByWorkflowNameAndVersion("test-workflow", "3.0"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
                workflowDefinitionService.activateVersion("test-workflow", "3.0", "test-user"));
    }

    @Test
    void deactivateWorkflow_ActiveWorkflow_DeactivatesWorkflow() {
        when(workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue("test-workflow"))
                .thenReturn(Optional.of(testWorkflow));

        workflowDefinitionService.deactivateWorkflow("test-workflow");

        assertFalse(testWorkflow.getIsActive());
        verify(workflowDefinitionRepository).save(testWorkflow);
    }

    @Test
    void deactivateWorkflow_NoActiveWorkflow_ThrowsException() {
        when(workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue("test-workflow"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> 
                workflowDefinitionService.deactivateWorkflow("test-workflow"));
    }
}
