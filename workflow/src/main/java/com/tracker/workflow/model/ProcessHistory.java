package com.tracker.workflow.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity for tracking workflow process history.
 */
@Entity
@Table(name = "process_history")
@Data
@NoArgsConstructor
public class ProcessHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;

    @Enumerated(EnumType.STRING)
    private WorkflowStates fromState;

    @Enumerated(EnumType.STRING)
    private WorkflowStates toState;

    @Enumerated(EnumType.STRING)
    private WorkflowEvents event;

    private String userId;

    private LocalDateTime timestamp;
    private String comments;

    @Type(JsonType.class)
    @Column(name = "context_data", columnDefinition = "jsonb")
    private Map<String, Object> contextData;
}
