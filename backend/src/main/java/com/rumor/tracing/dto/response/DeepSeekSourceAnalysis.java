package com.rumor.tracing.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekSourceAnalysis {
    private Double reliability;
    private String reputation;
    private List<String> concerns;
} 