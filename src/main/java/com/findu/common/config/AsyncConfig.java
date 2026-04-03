package com.findu.common.config;

import com.findu.common.trace.TraceIdHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * 异步任务配置。
 * 配置线程池，并支持子线程继承父线程的上下文（TraceId、MDC、RequestContext）。
 */
@Configuration
public class AsyncConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 创建异步任务执行器。
     * 配置了 TaskDecorator，确保子线程能够继承父线程的上下文：
     * - TraceId（ThreadLocal）
     * - MDC（日志上下文）
     * - RequestContext（Spring 请求上下文）
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("UserProfile-Async-");
        executor.setRejectedExecutionHandler((r, threadPoolExecutor) -> {
            LOGGER.error("Task rejected: {}", r.toString());
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 设置 TaskDecorator，用于传递线程上下文
        executor.setTaskDecorator(new ContextCopyingTaskDecorator());
        
        executor.initialize();
        return executor;
    }

    /**
     * 上下文复制装饰器，用于在异步任务中传递父线程的上下文。
     */
    private static class ContextCopyingTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 捕获父线程的上下文
            String traceId = TraceIdHolder.get();
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            
            return () -> {
                try {
                    // 在子线程中恢复上下文
                    if (traceId != null) {
                        TraceIdHolder.bind(traceId);
                    }
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    }
                    if (requestAttributes != null) {
                        RequestContextHolder.setRequestAttributes(requestAttributes, true);
                    }
                    
                    // 执行任务
                    runnable.run();
                } finally {
                    // 清理子线程的上下文
                    TraceIdHolder.clear();
                    MDC.clear();
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}

