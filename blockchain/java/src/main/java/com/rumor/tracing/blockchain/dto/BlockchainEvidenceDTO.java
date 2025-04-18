package com.rumor.tracing.blockchain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlockchainEvidenceDTO {
    private String id;
    private String hash;
    private String content;
    private String type;
    private String status;
    private LocalDateTime timestamp;
    private String transactionId;
    private String blockNumber;
    private String creator;
    private String signature;
    private Boolean verified;
    private String description;
}