package com.rumor.tracing.dto.request;

import com.rumor.tracing.entity.Rumor;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RumorCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String source;

    // 添加 toEntity 方法，将请求转换为实体
    public Rumor toEntity() {
        return Rumor.builder()
                .title(this.title)
                .content(this.content)
                .source(this.source)
                .status(Rumor.RumorStatus.PENDING)  // 默认状态
                .build();
    }
}
