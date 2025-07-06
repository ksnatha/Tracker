package com.tracker.workflow.repository;

import com.tracker.workflow.model.ProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessHistoryRepository extends JpaRepository<ProcessHistory, Long> {
    List<ProcessHistory> findByProcessInstanceIdOrderByTimestamp(String processInstanceId);
}