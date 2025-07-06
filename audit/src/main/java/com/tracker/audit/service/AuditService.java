package com.tracker.audit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for handling audit history.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AuditService {

    /**
     * Records an audit event.
     *
     * @param userId    the user ID
     * @param action    the action performed
     * @param entityId  the entity ID
     * @param details   additional details
     */
    public void recordAuditEvent(String userId, String action, String entityId, String details) {
        log.info("Recording audit event: User {}, Action {}, Entity {}", userId, action, entityId);
        // Implementation will be added later
    }
}