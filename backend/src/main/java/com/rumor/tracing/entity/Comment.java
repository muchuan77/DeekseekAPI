package com.rumor.tracing.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@SQLDelete(sql = "UPDATE comments SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rumor_id", nullable = false)
    private Rumor rumor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;

    @Column(name = "report_count")
    private Integer reportCount = 0;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
