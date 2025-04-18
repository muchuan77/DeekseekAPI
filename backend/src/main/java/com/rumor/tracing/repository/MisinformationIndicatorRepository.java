package com.rumor.tracing.repository;

import com.rumor.tracing.entity.MisinformationIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MisinformationIndicatorRepository extends JpaRepository<MisinformationIndicator, Long> {
}
