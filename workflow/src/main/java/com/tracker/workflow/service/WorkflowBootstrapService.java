package com.tracker.workflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class WorkflowBootstrapService implements ApplicationRunner {
    
    private final WorkflowDefinitionService workflowDefinitionService;
    private final WorkflowMigrationService migrationService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow").isEmpty()) {
            log.info("No active workflow found, performing initial migration");
            migrationService.migrateHardcodedWorkflowToDatabase();
            log.info("Initial workflow migration completed");
        } else {
            log.info("Active workflow found, skipping migration");
        }
    }
}
