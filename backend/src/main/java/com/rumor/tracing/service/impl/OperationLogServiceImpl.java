package com.rumor.tracing.service.impl;

import com.rumor.tracing.entity.LogOperation;
import com.rumor.tracing.repository.LogOperationRepository;
import com.rumor.tracing.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class OperationLogServiceImpl implements OperationLogService {

    private final LogOperationRepository operationLogRepository;

    @Autowired
    public OperationLogServiceImpl(LogOperationRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public LogOperation save(LogOperation operationLog) {
        return operationLogRepository.save(operationLog);
    }

    @Override
    public Page<LogOperation> findByConditions(String username, String operationType,
                                             LocalDateTime startTime, LocalDateTime endTime,
                                             Pageable pageable) {
        if (username != null && operationType != null) {
            return operationLogRepository.findByOperationTypeAndUsername(operationType, username, pageable);
        } else if (operationType != null) {
            return operationLogRepository.findByOperationType(operationType, pageable);
        } else if (username != null) {
            return operationLogRepository.findByUsername(username, pageable);
        } else if (startTime != null && endTime != null) {
            return operationLogRepository.findByExecutionTimeBetween(startTime, endTime, pageable);
        } else if (operationType != null && startTime != null && endTime != null) {
            return operationLogRepository.findByOperationTypeAndExecutionTimeBetween(operationType, startTime, endTime, pageable);
        }
        return operationLogRepository.findAll(pageable);
    }

    @Override
    public LogOperation findById(String id) {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation log not found with id: " + id));
    }

    @Override
    public void deleteById(String id) {
        operationLogRepository.deleteById(id);
    }

    @Override
    public void logOperation(String operationType, String description) {
        LogOperation log = new LogOperation();
        log.setOperationType(operationType);
        log.setDescription(description);
        log.setExecutionTime(LocalDateTime.now());
        operationLogRepository.save(log);
    }
} 