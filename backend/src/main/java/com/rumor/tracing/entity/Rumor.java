package com.rumor.tracing.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rumors")
@SQLDelete(sql = "UPDATE rumors SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
public class Rumor extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 500)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RumorStatus status;

    @Column(columnDefinition = "TEXT")
    private String verificationResult;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "rumor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "rumor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RumorAnalysis> analyses = new HashSet<>();

    @PrePersist
    protected void onPrePersist() {
        status = RumorStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

    public enum RumorStatus {
        PENDING,            // 待验证
        VERIFIED_TRUE,      // 已验证为真
        VERIFIED_FALSE,     // 已验证为假
        UNDER_INVESTIGATION // 调查中
    }
}