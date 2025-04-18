package com.rumor.tracing.blockchain.dto;

import lombok.Data;

@Data
public class BlockchainRequest {
    private String content;
    private String type;
    private String description;
} 