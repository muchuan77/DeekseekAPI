package com.rumor.tracing.service.impl;

import com.rumor.tracing.dto.CommentDTO;
import com.rumor.tracing.entity.Comment;
import com.rumor.tracing.entity.Rumor;
import com.rumor.tracing.entity.User;
import com.rumor.tracing.repository.CommentRepository;
import com.rumor.tracing.repository.RumorRepository;
import com.rumor.tracing.repository.UserRepository;
import com.rumor.tracing.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RumorRepository rumorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        
        Rumor rumor = rumorRepository.findById(commentDTO.getRumorId())
            .orElseThrow(() -> new RuntimeException("Rumor not found"));
        comment.setRumor(rumor);
        
        User user = userRepository.findById(commentDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        comment.setUser(user);
        
        comment = commentRepository.save(comment);
        return CommentDTO.from(comment);
    }

    @Override
    public List<CommentDTO> getComments(String rumorId) {
        Long rumorIdLong = Long.parseLong(rumorId);
        return getCommentsByRumorId(rumorIdLong, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional
    public void deleteComment(String commentId) {
        Long commentIdLong = Long.parseLong(commentId);
        commentRepository.deleteById(commentIdLong);
    }

    @Override
    public Page<CommentDTO> getCommentsByRumorId(Long rumorId, Pageable pageable) {
        return commentRepository.findByRumorId(rumorId, pageable)
                .map(CommentDTO::from);
    }
}
