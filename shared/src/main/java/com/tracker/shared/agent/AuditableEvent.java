package com.tracker.shared.agent;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class AuditableEvent {
    private String eventId;
    private LocalDateTime timestamp;

    public AuditableEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
}
