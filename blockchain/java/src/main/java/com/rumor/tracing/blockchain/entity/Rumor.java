package com.rumor.tracing.blockchain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Rumor {
    private Long id;
    private String content;
    private String source;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String creator;
    private Boolean verified;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String blockchainHash;
    private String blockchainTxId;
} 