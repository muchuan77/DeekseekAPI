package com.rumor.tracing.service;

import com.rumor.tracing.entity.LogOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OperationLogService {
    /**
     * 保存操作日志
     * @param operationLog 操作日志实体
     * @return 保存后的操作日志
     */
    LogOperation save(LogOperation operationLog);

    /**
     * 分页查询操作日志
     * @param username 用户名
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<LogOperation> findByConditions(String username, String operationType, 
                                      LocalDateTime startTime, LocalDateTime endTime, 
                                      Pageable pageable);

    /**
     * 根据ID查询操作日志
     * @param id 日志ID
     * @return 操作日志
     */
    LogOperation findById(String id);

    /**
     * 删除操作日志
     * @param id 日志ID
     */
    void deleteById(String id);

    /**
     * 记录操作日志
     * @param operationType 操作类型
     * @param description 操作描述
     */
    void logOperation(String operationType, String description);
} 