package com.rumor.tracing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekSourceAnalysis {
    @JsonProperty("reliability")
    private Double reliability;
    
    @JsonProperty("reputation")
    private String reputation;
    
    @JsonProperty("concerns")
    private List<String> concerns;
}
