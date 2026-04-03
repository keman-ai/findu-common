package com.findu.common.moderation.handler;

import com.findu.common.moderation.ContentModerationClient;
import com.findu.common.moderation.ModerationHandler;
import com.findu.common.moderation.ModerationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 审核处理器抽象基类。
 * 提取公共的工具方法和异常处理逻辑。
 *
 * @param <T> 审核请求上下文类型
 * @param <R> 审核结果类型
 */
public abstract class AbstractModerationHandler<T, R> implements ModerationHandler<T, R> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ContentModerationClient contentModerationClient;
    protected final ModerationProperties moderationProperties;

    protected AbstractModerationHandler(ContentModerationClient contentModerationClient,
                                        ModerationProperties moderationProperties) {
        this.contentModerationClient = contentModerationClient;
        this.moderationProperties = moderationProperties;
    }

    /**
     * 构建回调URL的通用方法。
     *
     * @param callbackBaseUrl 回调基础URL
     * @param callbackPath    回调路径（如 "/profile/change-requests/audit"）
     * @return 完整的回调URL
     */
    protected String buildCallbackUrl(String callbackBaseUrl, String callbackPath) {
        if (!StringUtils.hasText(callbackBaseUrl)) {
            return null;
        }
        return trimTrailingSlash(callbackBaseUrl) + callbackPath;
    }

    public Map<String, Object> parseAuditResult(String reviewId, List<Map<String, Object>> reviewDetails, Map<String, Object> callbackContext) {
        return Map.of();
    }

    /**
     * 去除URL末尾的斜杠。
     */
    protected String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    /**
     * 规范化字符串（转小写并trim）。
     */
    protected String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    /**
     * 安全地获取字符串值，如果为空则返回默认值。
     */
    protected String defaultString(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    /**
     * 安全地获取字符串值，如果为空则返回空字符串。
     */
    protected String defaultString(String value) {
        return defaultString(value, "");
    }

    /**
     * 规范化审核状态字符串。
     */
    protected String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "pending";
        }
        String normalized = normalize(status);
        if (normalized.contains("approved")) {
            return "approved";
        }
        if (normalized.contains("rejected")) {
            return "rejected";
        }
        return "pending";
    }
}
