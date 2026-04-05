package com.findu.common.moderation;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 内容审核客户端配置。
 */
@ConfigurationProperties(prefix = "moderation")
public class ModerationProperties {

    /**
     * 审核服务基础地址。
     */
    private String baseUrl;

    /**
     * 回调基础地址。
     */
    private String callbackBaseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCallbackBaseUrl() {
        return callbackBaseUrl;
    }

    public void setCallbackBaseUrl(String callbackBaseUrl) {
        this.callbackBaseUrl = callbackBaseUrl;
    }
}

