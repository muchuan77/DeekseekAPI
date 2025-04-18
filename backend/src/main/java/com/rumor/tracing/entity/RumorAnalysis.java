package com.rumor.tracing.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.rumor.tracing.model.AnalysisType;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rumor_analyses")
@EqualsAndHashCode(callSuper = true)
public class RumorAnalysis extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rumor_id", nullable = false)
    private Rumor rumor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AnalysisType type;
    
    @Column(columnDefinition = "TEXT")
    private String result;
    
    @Column(nullable = false)
    private Double confidence;
    
    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "analysis_model", length = 100)
    private String analysisModel;

    @Column(name = "analysis_version", length = 50)
    private String analysisVersion;

    @Column(name = "analysis_parameters", columnDefinition = "TEXT")
    private String analysisParameters;

    @Column(name = "analysis_duration")
    private Long analysisDuration;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "status", length = 20)
    private String status;

    @PrePersist
    protected void onPrePersist() {
        analyzedAt = LocalDateTime.now();
        if (status == null) {
            status = "COMPLETED";
        }
    }
} 