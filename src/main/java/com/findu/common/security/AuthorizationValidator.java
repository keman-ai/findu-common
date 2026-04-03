package com.findu.common.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 授权校验接口，负责从请求中解析用户标识等。
 */
public interface AuthorizationValidator {

    /**
     * 从请求中获取 x-user-id 请求头（忽略大小写），并对值做 trim。
     * 无该头或值为空/仅空白时返回 null。
     *
     * @param request HTTP 请求
     * @return 有值则返回 trim 后的用户唯一标识，无或空则返回 null
     */
    String getUserIdFromRequest(HttpServletRequest request);

    /**
     * 从请求中获取 x-agent-id 请求头（忽略大小写），并对值做 trim。
     * 无该头或值为空/仅空白时返回 null。
     *
     * @param request HTTP 请求
     * @return 有值则返回 trim 后的字符串，无或空则返回 null
     */
    String getAgentIdFromRequest(HttpServletRequest request);

}
