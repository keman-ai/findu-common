package com.findu.common.autoconfigure;

import com.findu.common.config.AsyncConfig;
import com.findu.common.config.RestTemplateConfig;
import com.findu.common.config.WebMvcConfig;
import com.findu.common.event.EventPublisher;
import com.findu.common.event.bus.DomainEventBus;
import com.findu.common.event.publisher.DomainEventPublisherImpl;
import com.findu.common.exception.GlobalExceptionHandler;
import com.findu.common.extend.ExtendInfoProcessor;
import com.findu.common.extend.ExtendInfoProcessorRegistry;
import com.findu.common.extend.impl.EmptyExtendInfoProcessor;
import com.findu.common.image.ImageInfoClient;
import com.findu.common.image.impl.OSSImageInfoClientImpl;
import com.findu.common.security.AuthorizationValidator;
import com.findu.common.security.HeaderAuthorizationValidator;
import com.findu.common.trace.RequestLoggingFilter;
import com.findu.common.trace.SchedulerTraceIdAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "findu.core.enabled", havingValue = "true", matchIfMissing = true)
@Import({AsyncConfig.class, WebMvcConfig.class})
public class FinduCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "requestLoggingFilter")
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    public SchedulerTraceIdAspect schedulerTraceIdAspect() {
        return new SchedulerTraceIdAspect();
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationValidator.class)
    public HeaderAuthorizationValidator headerAuthorizationValidator() {
        return new HeaderAuthorizationValidator();
    }

    @Bean
    @ConditionalOnMissingBean(EventPublisher.class)
    public DomainEventBus domainEventBus(ApplicationEventPublisher publisher) {
        return new DomainEventBus(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainEventPublisherImpl domainEventPublisherImpl(EventPublisher publisher) {
        return new DomainEventPublisherImpl(publisher);
    }

    @Bean
    @ConditionalOnMissingBean(ImageInfoClient.class)
    @ConditionalOnClass(name = "org.apache.hc.client5.http.classic.HttpClient")
    public OSSImageInfoClientImpl ossImageInfoClient(RestTemplate restTemplate) {
        return new OSSImageInfoClientImpl(restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public EmptyExtendInfoProcessor emptyExtendInfoProcessor() {
        return new EmptyExtendInfoProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtendInfoProcessorRegistry extendInfoProcessorRegistry(
            List<ExtendInfoProcessor> processors,
            EmptyExtendInfoProcessor defaultProcessor) {
        return new ExtendInfoProcessorRegistry(processors, defaultProcessor);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.apache.hc.client5.http.classic.HttpClient")
    @Import(RestTemplateConfig.class)
    static class RestTemplateImportConfiguration {
    }
}
