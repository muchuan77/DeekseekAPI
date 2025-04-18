package com.rumor.tracing.repository;

import com.rumor.tracing.entity.DeepSeekAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeepSeekAnalysisRepository extends JpaRepository<DeepSeekAnalysis, Long> {
    List<DeepSeekAnalysis> findByRumorId(Long rumorId);
    Optional<DeepSeekAnalysis> findTopByRumorIdOrderByCreatedAtDesc(Long rumorId);
} 