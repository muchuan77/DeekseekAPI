package com.rumor.tracing.aspect;

import com.rumor.tracing.entity.es.OperationLog;
import com.rumor.tracing.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final ElasticsearchOperations elasticsearchOperations;

    @Around("@annotation(com.rumor.tracing.annotation.LogOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            try {
                saveOperationLog(joinPoint, startTime, result, throwable);
            } catch (Exception e) {
                log.error("Error saving operation log", e);
            }
        }
    }

    private void saveOperationLog(ProceedingJoinPoint joinPoint, LocalDateTime startTime,
                                  Object result, Throwable throwable) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        OperationLog operationLog = OperationLog.builder()
                .timestamp(startTime)
                .username(Optional.ofNullable(SecurityUtils.getCurrentUsername()).orElse("anonymous"))
                .method(signature.getMethod().getName())
                .className(signature.getDeclaringTypeName())
                .parameters(Arrays.toString(joinPoint.getArgs()))
                .result(result != null ? result.toString() : null)
                .error(throwable != null ? throwable.getMessage() : null)
                .ipAddress(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .duration(Duration.between(startTime, LocalDateTime.now()).toMillis())
                .build();

        elasticsearchOperations.save(operationLog);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
