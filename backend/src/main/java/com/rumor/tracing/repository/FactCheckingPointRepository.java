package com.rumor.tracing.repository;

import com.rumor.tracing.entity.FactCheckingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactCheckingPointRepository extends JpaRepository<FactCheckingPoint, Long> {
}
