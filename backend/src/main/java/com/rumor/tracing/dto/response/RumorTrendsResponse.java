package com.rumor.tracing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RumorTrendsResponse {
    private LocalDate date;        // 日期
    private int rumorsPosted;      // 发布的谣言数量
    private int commentsPosted;    // 评论数量
    private int shares;            // 分享数量

    // 根据实际需求添加其他趋势相关字段
}
