package com.rumor.tracing.service;

import com.rumor.tracing.dto.DetectionResult;
import com.rumor.tracing.dto.request.DetectionRequest;

import java.util.List;

public interface DetectionService {
    DetectionResult analyzeRumor(DetectionRequest request);
    List<DetectionResult> getDetectionHistory(String startTime, String endTime);
    DetectionResult getDetectionResult(String id);
} 