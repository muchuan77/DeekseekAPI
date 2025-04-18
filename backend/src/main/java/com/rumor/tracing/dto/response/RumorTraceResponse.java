package com.rumor.tracing.dto.response;

import com.rumor.tracing.entity.Rumor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RumorTraceResponse {
    private Rumor rumor;
}
