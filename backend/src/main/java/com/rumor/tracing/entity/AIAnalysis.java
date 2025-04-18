package com.rumor.tracing.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "ai_analyses")
public class AIAnalysis extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "rumor_id", nullable = false)
    private Rumor rumor;

    private double credibilityScore;

    @Column(columnDefinition = "TEXT")
    private String analysisResult;

    @ElementCollection
    @CollectionTable(name = "fact_checking_points", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "point", columnDefinition = "TEXT")
    private List<String> factCheckingPoints;

    @ElementCollection
    @CollectionTable(name = "misinformation_indicators", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "indicator", columnDefinition = "TEXT")
    private List<String> misinformationIndicators;

    @Column(columnDefinition = "TEXT")
    private String sourceReliability;

    @Column(columnDefinition = "TEXT")
    private String verificationRecommendation;
} 