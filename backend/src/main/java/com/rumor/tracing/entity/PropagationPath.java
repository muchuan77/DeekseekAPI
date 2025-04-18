package com.rumor.tracing.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.rumor.tracing.model.PropagationType;

@Data
@Entity
@Table(name = "propagation_paths")
@EqualsAndHashCode(callSuper = true)
public class PropagationPath extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rumor_id", nullable = false)
    private Long rumorId;

    @Column(name = "source_node", nullable = false)
    private String sourceNode;

    @Column(name = "target_node", nullable = false)
    private String targetNode;

    @Column(name = "propagation_time", nullable = false)
    private Long propagationTime;

    @Column(name = "path_length", nullable = false)
    private Integer pathLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PropagationType type;
} 