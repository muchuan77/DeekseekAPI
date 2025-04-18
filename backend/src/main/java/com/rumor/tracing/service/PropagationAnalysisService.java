package com.rumor.tracing.service;

import com.rumor.tracing.entity.InfluenceAnalysis;
import com.rumor.tracing.entity.PropagationPath;
import java.util.List;
import java.util.Map;

public interface PropagationAnalysisService {
    
    /**
     * 分析传播路径
     * @param rumorId 谣言ID
     * @return 传播路径列表
     */
    List<PropagationPath> analyzePropagationPaths(Long rumorId);
    
    /**
     * 计算节点影响力
     * @param rumorId 谣言ID
     * @return 影响力分析结果
     */
    List<InfluenceAnalysis> calculateInfluence(Long rumorId);
    
    /**
     * 识别关键节点
     * @param rumorId 谣言ID
     * @return 关键节点列表
     */
    List<String> identifyKeyNodes(Long rumorId);
    
    /**
     * 获取传播网络图
     * @param rumorId 谣言ID
     * @return 网络图数据
     */
    Map<String, Object> getPropagationNetwork(Long rumorId);
}