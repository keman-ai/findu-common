package com.findu.common.cache;

import com.findu.common.cache.CurrencyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * findu-trade 币种接口 HTTP 客户端。
 * 调用 GET {trade.service.base-url}/api/v1/public/currencies 获取启用的币种列表。
 */
@Component
public class CurrencyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyClient.class);

    private static final String CURRENCIES_PATH = "/api/v1/public/currencies";

    private final RestTemplate restTemplate;

    @Value("${trade.service.base-url:}")
    private String tradeServiceBaseUrl;

    public CurrencyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 从 findu-trade 获取启用的币种列表。
     * 失败时记录警告日志并返回空列表，由调用方处理降级逻辑。
     *
     * @return 币种信息列表，失败时返回空列表
     */
    public List<CurrencyInfo> fetchCurrencies() {
        if (!StringUtils.hasText(tradeServiceBaseUrl)) {
            LOGGER.warn("trade.service.base-url 未配置，无法获取币种列表");
            return Collections.emptyList();
        }
        String endpoint = tradeServiceBaseUrl + CURRENCIES_PATH;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();
            if (body == null) {
                LOGGER.warn("获取币种列表响应为空，endpoint={}", endpoint);
                return Collections.emptyList();
            }
            Object data = body.get("data");
            if (!(data instanceof List<?> dataList)) {
                LOGGER.warn("获取币种列表响应 data 字段格式异常，endpoint={}", endpoint);
                return Collections.emptyList();
            }
            List<CurrencyInfo> result = new java.util.ArrayList<>();
            for (Object item : dataList) {
                if (item instanceof Map<?, ?> map) {
                    CurrencyInfo info = new CurrencyInfo();
                    info.setCode(asString(map.get("code")));
                    info.setSymbol(asString(map.get("symbol")));
                    info.setNameZh(asString(map.get("nameZh")));
                    info.setNameEn(asString(map.get("nameEn")));
                    result.add(info);
                }
            }
            return result;
        } catch (RestClientException ex) {
            LOGGER.warn("调用 findu-trade 币种接口失败，endpoint={}，原因: {}", endpoint, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private String asString(Object obj) {
        return obj instanceof String s ? s : null;
    }
}
