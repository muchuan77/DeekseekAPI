package com.rumor.tracing.service.impl;

import com.rumor.tracing.entity.InfluenceAnalysis;
import com.rumor.tracing.entity.PropagationPath;
import com.rumor.tracing.model.PropagationType;
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
        List<PropagationPath> paths = analyzePropagationPaths(rumorId);
        
        // 计算每个节点的影响力分数
        Map<String, Double> influenceScores = new HashMap<>();
        Map<String, Integer> pathCounts = new HashMap<>();
        
        for (PropagationPath path : paths) {
            // 计算源节点影响力
            String sourceNode = path.getSourceNode();
            double sourceScore = calculateNodeScore(path, true);
            influenceScores.merge(sourceNode, sourceScore, Double::sum);
            pathCounts.merge(sourceNode, 1, Integer::sum);
            
            // 计算目标节点影响力
            String targetNode = path.getTargetNode();
            double targetScore = calculateNodeScore(path, false);
            influenceScores.merge(targetNode, targetScore, Double::sum);
            pathCounts.merge(targetNode, 1, Integer::sum);
        }
        
        // 创建影响力分析结果
        List<InfluenceAnalysis> analyses = new ArrayList<>();
        for (Map.Entry<String, Double> entry : influenceScores.entrySet()) {
            String nodeId = entry.getKey();
            InfluenceAnalysis analysis = new InfluenceAnalysis();
            analysis.setNodeId(nodeId);
            analysis.setInfluenceScore(entry.getValue());
            analysis.setPathCount(pathCounts.get(nodeId));
            analysis.setIsKeyNode(isKeyNode(entry.getValue(), paths.size()));
            analyses.add(analysis);
        }
        
        // 按影响力分数排序
        analyses.sort(Comparator.comparing(InfluenceAnalysis::getInfluenceScore).reversed());
        
        return analyses;
    }
    
    private double calculateNodeScore(PropagationPath path, boolean isSource) {
        double baseScore = 1.0;
        double timeFactor = calculateTimeFactor(path.getPropagationTime());
        double typeFactor = calculateTypeFactor(path.getType());
        
        // 源节点的影响力权重更高
        double sourceWeight = isSource ? 1.5 : 1.0;
        
        return baseScore * timeFactor * typeFactor * sourceWeight;
    }
    
    private double calculateTimeFactor(Long propagationTime) {
        // 越早的传播时间权重越高
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - propagationTime;
        return Math.max(0.1, 1.0 - (timeDiff / (24.0 * 60 * 60 * 1000))); // 24小时衰减
    }
    
    private double calculateTypeFactor(PropagationType type) {
        // 不同类型的传播行为权重不同
        switch (type) {
            case FORWARD:
                return 1.5; // 转发权重最高
            case COMMENT:
                return 1.2; // 评论次之
            case LIKE:
                return 1.0; // 点赞权重最低
            default:
                return 1.0;
        }
    }
    
    private boolean isKeyNode(double influenceScore, int totalPaths) {
        // 如果节点的影响力分数超过平均值的3倍，则认为是关键节点
        double averageScore = 2.0 / totalPaths; // 每个节点平均有2条边
        return influenceScore > averageScore * 3;
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
    
    @Override
    public List<Map<String, Object>> analyzePropagationTrends(Long rumorId, String type) {
        List<PropagationPath> paths = analyzePropagationPaths(rumorId);
        
        Map<String, Long> trendMap = paths.stream()
            .collect(Collectors.groupingBy(
                path -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(path.getPropagationTime());
                    if ("hour".equals(type)) {
                        return String.format("%02d:00", cal.get(Calendar.HOUR_OF_DAY));
                    } else {
                        return String.format("%04d-%02d-%02d", 
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH));
                    }
                },
                Collectors.counting()
            ));
        
        // 确保时间序列完整
        if ("hour".equals(type)) {
            for (int i = 0; i < 24; i++) {
                String hour = String.format("%02d:00", i);
                trendMap.putIfAbsent(hour, 0L);
            }
        } else {
            // 获取最早和最晚的日期
            Optional<Long> minTime = paths.stream()
                .map(PropagationPath::getPropagationTime)
                .min(Long::compareTo);
            Optional<Long> maxTime = paths.stream()
                .map(PropagationPath::getPropagationTime)
                .max(Long::compareTo);
            
            if (minTime.isPresent() && maxTime.isPresent()) {
                Calendar start = Calendar.getInstance();
                start.setTimeInMillis(minTime.get());
                Calendar end = Calendar.getInstance();
                end.setTimeInMillis(maxTime.get());
                
                while (!start.after(end)) {
                    String date = String.format("%04d-%02d-%02d", 
                        start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH) + 1,
                        start.get(Calendar.DAY_OF_MONTH));
                    trendMap.putIfAbsent(date, 0L);
                    start.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }
        
        // 转换为前端需要的格式并按时间排序
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