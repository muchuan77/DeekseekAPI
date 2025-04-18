package com.rumor.tracing.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "influence_analysis")
public class InfluenceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rumor_id", nullable = false)
    private Rumor rumor;

    @Column(nullable = false)
    private Double influenceScore;

    @Column(nullable = false)
    private Integer userCount;

    @Column(nullable = false)
    private Integer contentCount;

    @Column(name = "is_key_node", nullable = false)
    private Boolean isKeyNode;

    @ElementCollection
    @CollectionTable(name = "influence_analysis_user_influence", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "user_influence")
    private List<String> userInfluence;

    @ElementCollection
    @CollectionTable(name = "influence_analysis_content_influence", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "content_influence")
    private List<String> contentInfluence;

    @Column(nullable = false)
    private LocalDateTime analysisTime;

    @Column(nullable = false)
    private String analysisStatus;

    @Column
    private String analysisResult;

    @PrePersist
    protected void onCreate() {
        analysisTime = LocalDateTime.now();
    }
}