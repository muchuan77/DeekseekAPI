package com.rumor.tracing.dto.response;

import com.rumor.tracing.entity.Rumor;
import lombok.Data;

@Data
public class RumorDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String source;
    private String status;
    private boolean verified;
    private String verificationResult;

    // 添加 from 方法，将 Rumor 转换为 RumorDetailResponse
    public static RumorDetailResponse from(Rumor rumor) {
        RumorDetailResponse response = new RumorDetailResponse();
        response.setId(rumor.getId());
        response.setTitle(rumor.getTitle());
        response.setContent(rumor.getContent());
        response.setSource(rumor.getSource());
        response.setStatus(rumor.getStatus().toString());
        response.setVerified(rumor.getVerified());
        response.setVerificationResult(rumor.getVerificationResult());
        return response;
    }
}
