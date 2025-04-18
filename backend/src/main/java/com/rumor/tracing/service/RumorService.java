package com.rumor.tracing.service;

import com.rumor.tracing.dto.RumorDTO;
import com.rumor.tracing.entity.Rumor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RumorService {
    RumorDTO createRumor(RumorDTO rumor);
    List<RumorDTO> getRumors();
    RumorDTO getRumor(String id);
    RumorDTO updateRumor(String id, RumorDTO rumor);
    RumorDTO updateRumorStatus(String id, String status);
    void deleteRumor(String id);
    
    // 新增搜索方法
    Page<RumorDTO> searchRumors(String keyword, Rumor.RumorStatus status, Pageable pageable);
    Page<RumorDTO> searchRumorsByKeyword(String keyword, Pageable pageable);
    Page<RumorDTO> searchRumorsByStatus(Rumor.RumorStatus status, Pageable pageable);
}
