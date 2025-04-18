package com.rumor.tracing.service.impl;

import com.rumor.tracing.entity.OperationLog;
import com.rumor.tracing.repository.OperationLogRepository;
import com.rumor.tracing.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    @Override
    public void logOperation(String operationType, String operationDetail) {
        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setDescription(operationDetail);
        log.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        operationLogRepository.save(log);
    }
} 