package com.rumor.tracing.service.impl;

import com.rumor.tracing.dto.RumorDTO;
import com.rumor.tracing.entity.Rumor;
import com.rumor.tracing.repository.RumorRepository;
import com.rumor.tracing.service.RumorService;
import com.rumor.tracing.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RumorServiceImpl implements RumorService {

    private final RumorRepository rumorRepository;
    

    @Override
    @Transactional
    public RumorDTO createRumor(RumorDTO rumorDTO) {
        Rumor rumor = new Rumor();
        rumor.setTitle(rumorDTO.getTitle());
        rumor.setContent(rumorDTO.getContent());
        rumor.setSource(rumorDTO.getSource());
        rumor.setStatus(Rumor.RumorStatus.PENDING);
        Rumor savedRumor = rumorRepository.save(rumor);
        return convertToDTO(savedRumor);
    }

    @Override
    public List<RumorDTO> getRumors() {
        return rumorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RumorDTO getRumor(String id) {
        try {
            Long rumorId = Long.parseLong(id);
            return convertToDTO(rumorRepository.findById(rumorId)
                    .orElseThrow(() -> new BusinessException("谣言不存在")));
        } catch (NumberFormatException e) {
            throw new BusinessException("无效的谣言ID");
        }
    }

    @Override
    public RumorDTO updateRumorStatus(String id, String status) {
        Long rumorId = Long.parseLong(id);
        Rumor rumor = rumorRepository.findById(rumorId)
                .orElseThrow(() -> new BusinessException("谣言不存在"));
        
        try {
            // 将前端传入的状态值转换为正确的枚举值
            Rumor.RumorStatus newStatus = Rumor.RumorStatus.valueOf(status.toUpperCase());
            rumor.setStatus(newStatus);
            return convertToDTO(rumorRepository.save(rumor));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的状态值: " + status);
        }
    }

    @Override
    @Transactional
    public RumorDTO updateRumor(String id, RumorDTO rumorDTO) {
        Long rumorId = Long.parseLong(id);
        Rumor rumor = rumorRepository.findById(rumorId)
                .orElseThrow(() -> new RuntimeException("Rumor not found"));
        
        rumor.setTitle(rumorDTO.getTitle());
        rumor.setContent(rumorDTO.getContent());
        rumor.setSource(rumorDTO.getSource());
        
        return convertToDTO(rumorRepository.save(rumor));
    }

    @Override
    @Transactional
    public void deleteRumor(String id) {
        try {
            // 验证ID格式
            if (id == null || id.trim().isEmpty()) {
                throw new BusinessException("谣言ID不能为空");
            }

            Long rumorId;
            try {
                rumorId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new BusinessException("无效的谣言ID格式");
            }

            // 检查谣言是否存在
            if (!rumorRepository.existsById(rumorId)) {
                throw new BusinessException("谣言不存在");
            }

            // 检查是否有关联数据
            long commentCount = rumorRepository.countCommentsByRumorId(rumorId);
            if (commentCount > 0) {
                throw new BusinessException("该谣言存在关联评论，请先删除相关评论");
            }

            long analysisCount = rumorRepository.countAnalysesByRumorId(rumorId);
            if (analysisCount > 0) {
                throw new BusinessException("该谣言存在关联分析，请先删除相关分析");
            }

            // 执行删除
            try {
                // 先尝试软删除
                Rumor rumor = rumorRepository.findById(rumorId)
                        .orElseThrow(() -> new BusinessException("谣言不存在"));
                rumor.setDeleted(true);
                rumorRepository.save(rumor);
            } catch (Exception e) {
                // 如果软删除失败，尝试硬删除
                try {
                    rumorRepository.deleteById(rumorId);
                } catch (Exception ex) {
                    throw new BusinessException("删除谣言失败: " + ex.getMessage());
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("系统错误: " + e.getMessage());
        }
    }

    @Override
    public Page<RumorDTO> searchRumors(String keyword, Rumor.RumorStatus status, Pageable pageable) {
        Page<Rumor> rumors = rumorRepository.searchByKeywordAndStatus(keyword, status, pageable);
        return rumors.map(this::convertToDTO);
    }

    @Override
    public Page<RumorDTO> searchRumorsByKeyword(String keyword, Pageable pageable) {
        Page<Rumor> rumors = rumorRepository.searchByKeyword(keyword, pageable);
        return rumors.map(this::convertToDTO);
    }

    @Override
    public Page<RumorDTO> searchRumorsByStatus(Rumor.RumorStatus status, Pageable pageable) {
        Page<Rumor> rumors = rumorRepository.findByStatus(status, pageable);
        return rumors.map(this::convertToDTO);
    }

    private RumorDTO convertToDTO(Rumor rumor) {
        RumorDTO dto = new RumorDTO();
        dto.setId(rumor.getId().toString());
        dto.setTitle(rumor.getTitle());
        dto.setContent(rumor.getContent());
        dto.setSource(rumor.getSource());
        dto.setStatus(rumor.getStatus().name());
        dto.setVerificationResult(rumor.getVerificationResult());
        dto.setPublishTime(rumor.getCreatedAt());
        dto.setVerifyTime(rumor.getVerifiedAt());
        dto.setVerifier(rumor.getVerifiedBy() != null ? rumor.getVerifiedBy().toString() : null);
        dto.setCreator(rumor.getCreatedBy() != null ? rumor.getCreatedBy().toString() : null);
        return dto;
    }
}

