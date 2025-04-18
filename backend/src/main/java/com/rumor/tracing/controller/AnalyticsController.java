package com.rumor.tracing.controller;

import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.dto.response.RumorStatisticsResponse;
import com.rumor.tracing.dto.response.RumorTrendsResponse;
import com.rumor.tracing.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "数据分析", description = "谣言数据分析相关接口")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据", description = "获取谣言的统计信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResponse<RumorStatisticsResponse> getStatistics(
            @Parameter(description = "时间范围") @RequestParam(required = false) String timeRange) {
        return ApiResponse.success(analyticsService.getStatistics(timeRange));
    }

    @GetMapping("/trends")
    @Operation(summary = "获取趋势数据", description = "获取谣言的趋势信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResponse<RumorTrendsResponse> getTrends(
            @Parameter(description = "时间范围") @RequestParam(required = false) String timeRange) {
        return ApiResponse.success(analyticsService.getTrends(timeRange));
    }
}
