package com.rumor.tracing.service;

public interface OperationLogService {
    void logOperation(String operationType, String operationDetail);
} 