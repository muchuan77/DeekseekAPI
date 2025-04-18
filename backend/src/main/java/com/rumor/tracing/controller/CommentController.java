package com.rumor.tracing.controller;

import com.rumor.tracing.dto.CommentDTO;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.dto.response.PageResponse;
import com.rumor.tracing.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "评论管理相关接口")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "创建评论")
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@RequestBody CommentDTO comment) {
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(comment)));
    }

    @GetMapping
    @Operation(summary = "获取评论列表")
    public ResponseEntity<ApiResponse<PageResponse<CommentDTO>>> getComments(
            @Parameter(description = "谣言ID") @RequestParam(required = true) String rumorId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<CommentDTO> comments = commentService.getCommentsByRumorId(
            Long.parseLong(rumorId), 
            PageRequest.of(page, size)
        );
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(comments)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
