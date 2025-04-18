package com.rumor.tracing.repository;

import com.rumor.tracing.entity.InfluenceAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluenceAnalysisRepository extends JpaRepository<InfluenceAnalysis, Long> {
    
    Page<InfluenceAnalysis> findByRumorId(Long rumorId, Pageable pageable);
    
    Page<InfluenceAnalysis> findByRumorIdAndIsKeyNode(Long rumorId, Boolean isKeyNode, Pageable pageable);
    
    @Query("SELECT i FROM InfluenceAnalysis i WHERE " +
           "i.rumor.id = :rumorId AND " +
           "i.influenceScore >= :minScore")
    Page<InfluenceAnalysis> findHighInfluenceNodes(@Param("rumorId") Long rumorId,
                                                 @Param("minScore") Integer minScore,
                                                 Pageable pageable);
} 