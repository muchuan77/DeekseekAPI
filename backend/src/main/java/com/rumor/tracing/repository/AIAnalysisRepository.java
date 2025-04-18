package com.rumor.tracing.repository;

import com.rumor.tracing.entity.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Long> {
    Optional<AIAnalysis> findByRumorId(Long rumorId);
} 