package com.findu.common.cache;

import com.findu.common.cache.CurrencyClient;
import com.findu.common.cache.CurrencyInfo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 启用币种本地缓存。
 * <p>
 * 启动时（@PostConstruct）从 findu-trade 加载一次；
 * 之后每 30 分钟（可通过 currency.cache.refresh-interval-minutes 调整）刷新。
 * 刷新失败时保留上次缓存值，保证服务降级可用。
 * </p>
 */
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
@Component
public class CurrencyCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyCache.class);

    /**
     * 内置降级兜底集合，仅在首次加载失败且缓存为空时生效。
     */
    private static final Set<String> FALLBACK_CODES = Set.of("CNY", "USD");

    private final CurrencyClient currencyClient;

    /**
     * 线程安全的缓存引用。null 表示尚未成功加载过。
     */
    private final AtomicReference<Set<String>> enabledCodesRef = new AtomicReference<>(null);

    public CurrencyCache(CurrencyClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedDelay = 30, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public void scheduledRefresh() {
        refresh();
    }

    /**
     * 刷新缓存。失败时保留上一次的缓存值。
     */
    private void refresh() {
        try {
            List<CurrencyInfo> currencies = currencyClient.fetchCurrencies();
            if (currencies.isEmpty()) {
                LOGGER.warn("币种列表返回为空，保留上次缓存值");
                return;
            }
            Set<String> codes = currencies.stream()
                    .map(CurrencyInfo::getCode)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toUnmodifiableSet());
            enabledCodesRef.set(codes);
            LOGGER.info("币种列表加载成功: {}", codes);
        } catch (Exception ex) {
            LOGGER.warn("币种列表刷新失败，保留上次缓存值，原因: {}", ex.getMessage());
        }
    }

    /**
     * 返回当前缓存的启用币种代码集合。
     * 若尚未成功加载（启动时就失败），返回内置降级集合。
     */
    public Set<String> getEnabledCodes() {
        Set<String> cached = enabledCodesRef.get();
        return cached != null ? cached : FALLBACK_CODES;
    }

    /**
     * 检查指定币种代码是否有效（启用）。
     *
     * @param code 币种代码，如 "CNY"
     * @return true 表示有效
     */
    public boolean isValid(String code) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        return getEnabledCodes().contains(code);
    }
}
