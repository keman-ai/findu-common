package com.findu.common.cache.config;

import com.findu.common.cache.impl.RedisProperties;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类，提供连接池管理能力。
 */
public class RedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    private final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * 创建 Redis 连接工厂，配置连接池。
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setDatabase(redisProperties.getDatabase());
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            configuration.setPassword(redisProperties.getPassword());
        }

        // 配置连接池
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        if (redisProperties.getPool().getMaxWait() > 0) {
            poolConfig.setMaxWait(Duration.ofMillis(redisProperties.getPool().getMaxWait()));
        }

        // 配置客户端选项
        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(SocketOptions.builder()
                        .connectTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                        .build())
                .timeoutOptions(TimeoutOptions.builder()
                        .fixedTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                        .build())
                .build();

        LettucePoolingClientConfiguration poolingClientConfiguration =
                LettucePoolingClientConfiguration.builder()
                        .poolConfig(poolConfig)
                        .clientOptions(clientOptions)
                        .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, poolingClientConfiguration);
        factory.setValidateConnection(true);

        LOGGER.info("Redis连接工厂创建完成: host={}, port={}, database={}", 
                redisProperties.getHost(), redisProperties.getPort(), redisProperties.getDatabase());
        LOGGER.info("Redis连接池配置: maxActive={}, maxIdle={}, minIdle={}, maxWait={}ms, timeout={}ms",
                redisProperties.getPool().getMaxActive(),
                redisProperties.getPool().getMaxIdle(),
                redisProperties.getPool().getMinIdle(),
                redisProperties.getPool().getMaxWait(),
                redisProperties.getTimeout());

        return factory;
    }

    /**
     * 创建 RedisTemplate Bean。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 String 序列化器作为 key 的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用 JSON 序列化器作为 value 的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();

        LOGGER.info("RedisTemplate创建完成，已启用连接池管理");

        return template;
    }
}

