package com.tracker.workflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_roles")
@Data
@NoArgsConstructor
public class WorkflowRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @Column(name = "display_name")
    private String displayName;

    private String description;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserRole> userRoles;
}
