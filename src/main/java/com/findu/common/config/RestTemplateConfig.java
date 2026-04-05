package com.findu.common.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置类，使用HttpClient 5提供连接池管理能力。
 */
public class RestTemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateConfig.class);

    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * 连接超时时间（毫秒），默认5000
     */
    @Value("${spring.http.client.connection-timeout:5000}")
    private long connectionTimeout;

    /**
     * 读取超时时间（毫秒），默认10000
     */
    @Value("${spring.http.client.read-timeout:10000}")
    private long readTimeout;

    /**
     * 连接池最大连接数，默认200
     */
    @Value("${spring.http.client.pool.max-total:200}")
    private int maxTotal;

    /**
     * 每个路由的最大连接数，默认50
     */
    @Value("${spring.http.client.pool.default-max-per-route:50}")
    private int defaultMaxPerRoute;

    /**
     * 连接存活时间（毫秒），默认60000（60秒）
     */
    @Value("${spring.http.client.pool.time-to-live:60000}")
    private long timeToLive;

    /**
     * 创建连接池管理器。
     */
    @Bean
    @ConditionalOnMissingBean(name = "connectionManager")
    public PoolingHttpClientConnectionManager connectionManager() {
        connectionManager = new PoolingHttpClientConnectionManager();
        
        // 设置最大连接数
        connectionManager.setMaxTotal(maxTotal);
        
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        
        // 配置Socket参数
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.of(readTimeout, TimeUnit.MILLISECONDS))
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);
        
        LOGGER.info("HttpClient连接池配置: maxTotal={}, maxPerRoute={}, readTimeout={}ms",
                maxTotal, defaultMaxPerRoute, readTimeout);
        
        return connectionManager;
    }

    /**
     * 创建带连接池的HttpClient。
     */
    @Bean
    @ConditionalOnMissingBean(name = "httpClient")
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {
        // 配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.of(readTimeout, TimeUnit.MILLISECONDS))
                .setConnectionRequestTimeout(Timeout.of(connectionTimeout, TimeUnit.MILLISECONDS))
                .build();
        
        // 创建HttpClient
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(Timeout.of(timeToLive, TimeUnit.MILLISECONDS))
                .evictExpiredConnections()
                .build();
        
        LOGGER.info("HttpClient创建完成: connectTimeout={}ms, responseTimeout={}ms, timeToLive={}ms",
                connectionTimeout, readTimeout, timeToLive);
        
        return httpClient;
    }

    /**
     * 创建RestTemplate Bean，使用连接池管理的HttpClient。
     * RestTemplateBuilder会自动使用这个Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);
        
        LOGGER.info("RestTemplate创建完成，已启用连接池管理");
        
        return restTemplate;
    }

    /**
     * 应用关闭时清理资源。
     */
    @PreDestroy
    public void destroy() {
        try {
            if (httpClient != null) {
                httpClient.close();
                LOGGER.info("HttpClient已关闭");
            }
            if (connectionManager != null) {
                connectionManager.close();
                LOGGER.info("连接池管理器已关闭");
            }
        } catch (Exception e) {
            LOGGER.error("关闭HttpClient资源时出错", e);
        }
    }
}

