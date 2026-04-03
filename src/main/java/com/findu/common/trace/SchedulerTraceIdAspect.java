package com.findu.common.trace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 定时任务 TraceId 切面，负责为定时任务生成并绑定 TraceId。
 */
@Aspect
@Component
@Order(1)
public class SchedulerTraceIdAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerTraceIdAspect.class);

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduledMethods() {
        // Pointcut for @Scheduled methods
    }

    @Around("scheduledMethods()")
    public Object bindTraceIdForScheduler(ProceedingJoinPoint joinPoint) throws Throwable {
        // 生成 traceId，格式：scheduler-{UUID}
        String traceId = UUID.randomUUID().toString().replace("-", "");

        // 绑定 traceId
        TraceIdHolder.bind(traceId);
        MDC.put(TraceIdHolder.MDC_KEY, traceId);

        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startAt = System.currentTimeMillis();
        LOGGER.info("type=scheduler-start||class={}||method={}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startAt;
            LOGGER.info("type=scheduler-end||class={}||method={}||cost={}", className, methodName, cost);
            return result;
        } catch (Throwable throwable) {
            long cost = System.currentTimeMillis() - startAt;
            LOGGER.info("type=scheduler-error||class={}||method={}||cost={}||message={}",
                    className, methodName, cost, throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            TraceIdHolder.clear();
            MDC.remove(TraceIdHolder.MDC_KEY);
        }
    }
}

