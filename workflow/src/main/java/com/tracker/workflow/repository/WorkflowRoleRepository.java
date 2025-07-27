package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowRoleRepository extends JpaRepository<WorkflowRole, Long> {
    
    Optional<WorkflowRole> findByRoleName(String roleName);
}
