package com.rumor.tracing.service.impl;

import com.rumor.tracing.entity.InfluenceAnalysis;
import com.rumor.tracing.entity.PropagationPath;
import com.rumor.tracing.repository.InfluenceAnalysisRepository;
import com.rumor.tracing.repository.PropagationPathRepository;
import com.rumor.tracing.repository.RumorRepository;
import com.rumor.tracing.service.PropagationAnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropagationAnalysisServiceImpl implements PropagationAnalysisService {
    
    private final PropagationPathRepository propagationPathRepository;
    private final InfluenceAnalysisRepository influenceAnalysisRepository;
    private final RumorRepository rumorRepository;
    
    public PropagationAnalysisServiceImpl(
            PropagationPathRepository propagationPathRepository,
            InfluenceAnalysisRepository influenceAnalysisRepository,
            RumorRepository rumorRepository) {
        this.propagationPathRepository = propagationPathRepository;
        this.influenceAnalysisRepository = influenceAnalysisRepository;
        this.rumorRepository = rumorRepository;
    }
    
    @Override
    public List<PropagationPath> analyzePropagationPaths(Long rumorId) {
        // 获取所有传播路径
        return propagationPathRepository.findByRumorId(rumorId, null).getContent();
    }
    
    @Override
    public List<InfluenceAnalysis> calculateInfluence(Long rumorId) {
        // 获取所有传播路径
        List<PropagationPath> paths = analyzePropagationPaths(rumorId);
        
        // 计算每个节点的影响力分数
        Map<String, Integer> influenceScores = new HashMap<>();
        for (PropagationPath path : paths) {
            // 增加源节点的影响力
            influenceScores.merge(path.getSourceNode(), 1, Integer::sum);
            // 增加目标节点的影响力
            influenceScores.merge(path.getTargetNode(), 1, Integer::sum);
        }
        
        // 创建影响力分析结果
        List<InfluenceAnalysis> analyses = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : influenceScores.entrySet()) {
            InfluenceAnalysis analysis = new InfluenceAnalysis();
            analysis.setRumor(rumorRepository.findById(rumorId).orElseThrow(() -> new RuntimeException("Rumor not found")));
            analysis.setInfluenceScore(entry.getValue().doubleValue());
            analysis.setUserCount(1);
            analysis.setContentCount(1);
            analysis.setAnalysisStatus("COMPLETED");
            analyses.add(analysis);
        }
        
        // 保存分析结果
        return influenceAnalysisRepository.saveAll(analyses);
    }
    
    @Override
    public List<String> identifyKeyNodes(Long rumorId) {
        // 获取影响力分析结果
        List<InfluenceAnalysis> analyses = influenceAnalysisRepository
                .findByRumorIdAndIsKeyNode(rumorId, true, null)
                .getContent();
        
        // 提取关键节点ID
        return analyses.stream()
                .map(analysis -> analysis.getUserInfluence().get(0))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getPropagationNetwork(Long rumorId) {
        // 获取所有传播路径
        List<PropagationPath> paths = analyzePropagationPaths(rumorId);
        
        // 构建网络图数据
        Map<String, Object> network = new HashMap<>();
        
        // 节点列表
        Set<String> nodes = new HashSet<>();
        for (PropagationPath path : paths) {
            nodes.add(path.getSourceNode());
            nodes.add(path.getTargetNode());
        }
        
        // 边列表
        List<Map<String, Object>> edges = paths.stream()
                .map(path -> {
                    Map<String, Object> edge = new HashMap<>();
                    edge.put("source", path.getSourceNode());
                    edge.put("target", path.getTargetNode());
                    edge.put("time", path.getPropagationTime());
                    return edge;
                })
                .collect(Collectors.toList());
        
        network.put("nodes", nodes);
        network.put("edges", edges);
        
        return network;
    }
    
    private boolean isKeyNode(int score, int totalNodes) {
        // 如果节点的影响力分数超过平均值的2倍，则认为是关键节点
        double averageScore = 2.0 / totalNodes; // 每个节点平均有2条边
        return score > averageScore * 2;
    }
    
    @Override
    public List<Map<String, Object>> analyzePropagationTrends(Long rumorId) {
        List<PropagationPath> paths = analyzePropagationPaths(rumorId);
        
        // 按时间分组统计传播数量
        Map<String, Long> trendMap = paths.stream()
            .collect(Collectors.groupingBy(
                path -> formatTime(path.getPropagationTime()),
                Collectors.counting()
            ));
        
        // 转换为前端需要的格式
        return trendMap.entrySet().stream()
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("time", entry.getKey());
                map.put("count", entry.getValue());
                return map;
            })
            .sorted(Comparator.comparing(m -> (String) m.get("time")))
            .collect(Collectors.toList());
    }
    
    private String formatTime(Long timestamp) {
        // 将时间戳格式化为日期字符串，例如：2024-01-01
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
    }
    
    @Override
    public Page<PropagationPath> getPropagationPaths(Long rumorId, Long startTime, Long endTime, Pageable pageable) {
        if (startTime != null && endTime != null) {
            return propagationPathRepository.findByRumorIdAndPropagationTimeBetween(
                rumorId, startTime, endTime, pageable);
        } else if (startTime != null) {
            return propagationPathRepository.findByRumorIdAndPropagationTimeGreaterThanEqual(
                rumorId, startTime, pageable);
        } else if (endTime != null) {
            return propagationPathRepository.findByRumorIdAndPropagationTimeLessThanEqual(
                rumorId, endTime, pageable);
        } else {
            return propagationPathRepository.findByRumorId(rumorId, pageable);
        }
    }
} 