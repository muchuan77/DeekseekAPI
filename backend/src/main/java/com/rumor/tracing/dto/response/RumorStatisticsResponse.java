package com.rumor.tracing.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RumorStatisticsResponse {
    private int totalRumors;   // 总的谣言数量
    private int totalComments; // 总的评论数量
    private int totalShares;   // 总的分享数量

    // 可以根据实际需求添加其他统计字段
}
