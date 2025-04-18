package com.rumor.tracing.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RumorVerifyRequest {
    private Long rumorId;

    @NotBlank
    private String verificationResult;
}
