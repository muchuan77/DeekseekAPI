package com.rumor.tracing.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentCreateRequest {

    @NotNull
    private Long rumorId;

    @NotBlank
    private String content;

    @NotNull
    private Long userId;
}
