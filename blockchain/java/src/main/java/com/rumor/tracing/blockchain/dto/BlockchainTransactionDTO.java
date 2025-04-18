package com.rumor.tracing.blockchain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlockchainTransactionDTO {
    private String id;
    private String evidenceId;
    private String transactionId;
    private String blockNumber;
    private String status;
    private LocalDateTime timestamp;
    private String creator;
    private String signature;
    private String description;
} 