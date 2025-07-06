package com.tracker.workflow.model;

import com.tracker.shared.util.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> contextData;
}
