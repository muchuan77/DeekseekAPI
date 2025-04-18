package com.rumor.tracing.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BlockchainRequest {
    @NotBlank(message = "Content cannot be empty")
    private String content;

    @NotBlank(message = "Type cannot be empty")
    @Size(max = 20, message = "Type must be at most 20 characters")
    private String type;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotBlank(message = "Creator cannot be empty")
    @Size(max = 50, message = "Creator must be at most 50 characters")
    private String creator;
} 