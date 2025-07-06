package com.tracker.workflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sending notifications to users.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class NotificationService {
    
    /**
     * Notifies a user with a message.
     *
     * @param userId the ID of the user to notify
     * @param message the notification message
     */
    void notifyUser(String userId, String message){
        log.info("notify user method called");
    }
}