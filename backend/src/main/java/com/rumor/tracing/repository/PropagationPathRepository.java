package com.rumor.tracing.repository;

import com.rumor.tracing.entity.PropagationPath;
import com.rumor.tracing.model.PropagationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropagationPathRepository extends JpaRepository<PropagationPath, Long> {
    
    Page<PropagationPath> findByRumorId(Long rumorId, Pageable pageable);
    
    Page<PropagationPath> findByRumorIdAndType(Long rumorId, PropagationType type, Pageable pageable);
    
    @Query("SELECT p FROM PropagationPath p WHERE " +
           "p.rumorId = :rumorId AND " +
           "(p.sourceNode = :nodeId OR p.targetNode = :nodeId)")
    Page<PropagationPath> findRelatedPaths(@Param("rumorId") Long rumorId, 
                                         @Param("nodeId") String nodeId, 
                                         Pageable pageable);

    List<PropagationPath> findByRumorIdAndType(Long rumorId, PropagationType type);
    List<PropagationPath> findBySourceNode(String sourceNode);
    List<PropagationPath> findByTargetNode(String targetNode);
    List<PropagationPath> findByRumorId(Long rumorId);
} 