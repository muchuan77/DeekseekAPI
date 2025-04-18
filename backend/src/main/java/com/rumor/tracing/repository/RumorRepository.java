package com.rumor.tracing.repository;

import com.rumor.tracing.entity.Rumor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RumorRepository extends JpaRepository<Rumor, Long> {
    Page<Rumor> findByStatus(Rumor.RumorStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Rumor r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Rumor> searchByKeyword(String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Rumor r WHERE " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "r.status = :status")
    Page<Rumor> searchByKeywordAndStatus(String keyword, Rumor.RumorStatus status, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.rumor.id = :rumorId")
    long countCommentsByRumorId(Long rumorId);

    @Query("SELECT COUNT(a) FROM RumorAnalysis a WHERE a.rumor.id = :rumorId")
    long countAnalysesByRumorId(Long rumorId);
} 