package com.tracker.workflow.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "workflow_states")
@Data
@NoArgsConstructor
public class WorkflowStateDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "state_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StateType stateType;

    @Column(name = "display_name")
    private String displayName;

    private String description;

    @Column(name = "state_order")
    private Integer stateOrder;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    public enum StateType {
        INITIAL, NORMAL, END
    }
}
