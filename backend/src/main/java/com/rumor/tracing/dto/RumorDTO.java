package com.rumor.tracing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RumorDTO {
    private String id;
    private String title;
    private String content;
    private String source;
    private String status;  // 谣言状态：待验证、已验证、已辟谣等
    private String verificationResult;  // 验证结果
    private LocalDateTime publishTime;  // 发布时间
    private LocalDateTime verifyTime;  // 验证时间
    private String verifier;  // 验证人
    private String creator;  // 创建人
    private String category;  // 谣言类别
    private Integer influenceScore;  // 影响力分数
    private String evidenceId;  // 关联的区块链存证ID
} 