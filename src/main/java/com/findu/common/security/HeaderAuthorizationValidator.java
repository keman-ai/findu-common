package com.findu.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 从 HTTP Header 中提取用户/Agent 标识。
 * Gateway 层已完成鉴权，这里只做 header 读取。
 */
@Component
public class HeaderAuthorizationValidator implements AuthorizationValidator {

    @Override
    public String getUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("X-User-ID");
        if (userId == null) userId = request.getHeader("x-user-id");
        return StringUtils.hasText(userId) ? userId.trim() : null;
    }

    @Override
    public String getAgentIdFromRequest(HttpServletRequest request) {
        String agentId = request.getHeader("X-Agent-Id");
        if (agentId == null) agentId = request.getHeader("x-agent-id");
        return StringUtils.hasText(agentId) ? agentId.trim() : null;
    }
}
