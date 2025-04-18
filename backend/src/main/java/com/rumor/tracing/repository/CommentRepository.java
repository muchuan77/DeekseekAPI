package com.rumor.tracing.repository;

import com.rumor.tracing.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByRumorId(Long rumorId, Pageable pageable);
    Page<Comment> findByUserId(Long userId, Pageable pageable);
} 