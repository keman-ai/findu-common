package com.findu.common.autoconfigure;

import com.findu.common.cache.CacheService;
import com.findu.common.cache.CurrencyCache;
import com.findu.common.cache.CurrencyClient;
import com.findu.common.cache.config.RedisConfig;
import com.findu.common.cache.impl.RedisCacheService;
import com.findu.common.cache.impl.RedisProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@ConditionalOnProperty(name = "findu.cache.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RedisProperties.class)
@Import(RedisConfig.class)
public class FinduCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public RedisCacheService redisCacheService(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.profiles.active:dev}") String activeProfile) {
        return new RedisCacheService(redisTemplate, activeProfile);
    }

    @Bean
    @ConditionalOnMissingBean
    public CurrencyClient currencyClient(RestTemplate restTemplate) {
        return new CurrencyClient(restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public CurrencyCache currencyCache(CurrencyClient currencyClient) {
        return new CurrencyCache(currencyClient);
    }
}
