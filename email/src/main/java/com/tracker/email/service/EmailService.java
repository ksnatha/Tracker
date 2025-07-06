package com.tracker.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    /**
     * Sends an email.
     *
     * @param to      the recipient
     * @param subject the subject
     * @param body    the body
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to {}, subject: {}", to, subject);
        // Implementation will be added later
    }
}