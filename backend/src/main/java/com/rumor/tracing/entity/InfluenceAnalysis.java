package com.rumor.tracing.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
@Entity
@Table(name = "influence_analysis")
public class InfluenceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rumor_id", nullable = false)
    private Long rumorId;

    @Column(name = "node_id", nullable = false)
    private String nodeId;

    @Column(name = "influence_score", nullable = false)
    private Double influenceScore;

    @Column(name = "path_count", nullable = false)
    private Integer pathCount;

    @Column(name = "is_key_node", nullable = false)
    private Boolean isKeyNode;

    @Column(name = "analysis_time", nullable = false)
    private Long analysisTime;

    @Column(nullable = false)
    private Integer userCount;

    @Column(nullable = false)
    private Integer contentCount;

    @ElementCollection
    @CollectionTable(name = "influence_analysis_user_influence", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "user_influence")
    private List<String> userInfluence;

    @ElementCollection
    @CollectionTable(name = "influence_analysis_content_influence", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "content_influence")
    private List<String> contentInfluence;

    @Column(name = "analysis_time_original", nullable = false)
    private LocalDateTime analysisTimeOriginal;

    @Column(name = "analysis_status", nullable = false)
    private String analysisStatus;

    @Column(name = "analysis_result")
    private String analysisResult;

    @PrePersist
    protected void onCreate() {
        analysisTimeOriginal = LocalDateTime.now();
        analysisTime = analysisTimeOriginal.toEpochSecond(ZoneOffset.UTC);
    }
}