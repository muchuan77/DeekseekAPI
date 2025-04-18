package com.rumor.tracing.controller;

import com.rumor.tracing.dto.RumorDTO;
import com.rumor.tracing.dto.response.ApiResponse;
import com.rumor.tracing.entity.Rumor;
import com.rumor.tracing.service.RumorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/rumors")
@RequiredArgsConstructor
@Tag(name = "谣言管理", description = "谣言管理相关接口")
public class RumorController {

    private final RumorService rumorService;

    @PostMapping
    @Operation(summary = "创建谣言记录")
    public ResponseEntity<ApiResponse<RumorDTO>> createRumor(@RequestBody RumorDTO rumor) {
        return ResponseEntity.ok(ApiResponse.success(rumorService.createRumor(rumor)));
    }

    @GetMapping
    @Operation(summary = "获取谣言列表")
    public ResponseEntity<ApiResponse<List<RumorDTO>>> getRumors() {
        return ResponseEntity.ok(ApiResponse.success(rumorService.getRumors()));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索谣言")
    public ResponseEntity<ApiResponse<Page<RumorDTO>>> searchRumors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Rumor.RumorStatus status,
            Pageable pageable) {
        if (keyword != null && status != null) {
            return ResponseEntity.ok(ApiResponse.success(rumorService.searchRumors(keyword, status, pageable)));
        } else if (keyword != null) {
            return ResponseEntity.ok(ApiResponse.success(rumorService.searchRumorsByKeyword(keyword, pageable)));
        } else if (status != null) {
            return ResponseEntity.ok(ApiResponse.success(rumorService.searchRumorsByStatus(status, pageable)));
        } else {
            List<RumorDTO> allRumors = rumorService.getRumors();
            List<RumorDTO> pagedRumors = allRumors.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(new PageImpl<>(pagedRumors, pageable, allRumors.size())));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取谣言详情")
    public ResponseEntity<ApiResponse<RumorDTO>> getRumor(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(rumorService.getRumor(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新谣言信息")
    public ResponseEntity<ApiResponse<RumorDTO>> updateRumor(@PathVariable String id, @RequestBody RumorDTO rumor) {
        return ResponseEntity.ok(ApiResponse.success(rumorService.updateRumor(id, rumor)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新谣言状态")
    public ResponseEntity<ApiResponse<RumorDTO>> updateRumorStatus(
            @PathVariable String id,
            @RequestParam @Pattern(regexp = "VERIFIED_TRUE|VERIFIED_FALSE|PENDING|UNDER_INVESTIGATION", message = "无效的状态值") String status) {
        return ResponseEntity.ok(ApiResponse.success(rumorService.updateRumorStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除谣言")
    public ResponseEntity<ApiResponse<Void>> deleteRumor(@PathVariable String id) {
        rumorService.deleteRumor(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
