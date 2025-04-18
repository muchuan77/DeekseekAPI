package com.rumor.tracing.blockchain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RumorHistoryDTO {
    private Long rumorId;
    private String operation;
    private String operator;
    private String details;
    private LocalDateTime timestamp;
    private String transactionId;
    private String blockHash;
} 