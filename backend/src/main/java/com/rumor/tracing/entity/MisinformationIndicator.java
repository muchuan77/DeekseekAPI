package com.rumor.tracing.entity;

import javax.persistence.*;

@Entity
public class MisinformationIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "analysis_id", nullable = false)
    private AIAnalysis aiAnalysis;

    @Column(nullable = false)
    private String indicator;

    // Getters and Setters
}
