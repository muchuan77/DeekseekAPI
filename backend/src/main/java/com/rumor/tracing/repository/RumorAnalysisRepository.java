package com.rumor.tracing.repository;

import com.rumor.tracing.entity.RumorAnalysis;
import com.rumor.tracing.model.AnalysisType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RumorAnalysisRepository extends JpaRepository<RumorAnalysis, Long> {
    
    Page<RumorAnalysis> findByRumorId(Long rumorId, Pageable pageable);
    
    Page<RumorAnalysis> findByRumorIdAndType(Long rumorId, AnalysisType type, Pageable pageable);
} 