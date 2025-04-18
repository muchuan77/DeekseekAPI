package com.rumor.tracing.service;

import com.rumor.tracing.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    List<CommentDTO> getComments(String rumorId);
    void deleteComment(String commentId);
    Page<CommentDTO> getCommentsByRumorId(Long rumorId, Pageable pageable);
}