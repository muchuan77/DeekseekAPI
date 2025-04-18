package com.rumor.tracing.dto;

import com.rumor.tracing.entity.Comment;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private Long rumorId;
    private Long userId;
    private String username;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer reportCount;
    private Boolean isVerified;
    private Long verifiedBy;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .rumorId(comment.getRumor().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .reportCount(comment.getReportCount())
                .isVerified(comment.getIsVerified())
                .verifiedBy(comment.getVerifiedBy())
                .verifiedAt(comment.getVerifiedAt())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
} 