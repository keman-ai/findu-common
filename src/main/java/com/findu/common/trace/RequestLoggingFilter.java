package com.findu.common.trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 统一的请求和响应日志记录过滤器。
 * 负责：
 * 1. TraceId 的生成、绑定、清理
 * 2. 请求体和响应体的缓存包装
 * 3. 统一的请求和响应日志记录
 */
@Component
@Order(1) // 确保最早执行
public class RequestLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger("findu-common");
    private static final int MAX_CONTENT_LENGTH = 1024 * 1024; // 1MB
    private static final int MAX_LOG_LENGTH = 2048;
    private static final List<String> TRACE_HEADER_CANDIDATES = List.of(
            TraceIdHolder.TRACE_ID_HEADER,
            "traceId",
            "Trace-Id",
            "X-B3-TraceId"
    );

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest) ||
                !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        // 1. 生成/获取 TraceId
        String traceId = resolveTraceId(httpRequest);
        TraceIdHolder.bind(traceId);
        MDC.put(TraceIdHolder.MDC_KEY, traceId);

        // 设置响应头
        httpResponse.setHeader(TraceIdHolder.TRACE_ID_HEADER, traceId);

        // 记录开始时间
        long startAt = System.currentTimeMillis();
        httpRequest.setAttribute(START_TIME_ATTRIBUTE, startAt);

        // 2. 包装请求体和响应体
        ContentCachingRequestWrapper wrappedRequest = null;
        String method = httpRequest.getMethod();
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            wrappedRequest = new ContentCachingRequestWrapper(httpRequest, MAX_CONTENT_LENGTH);
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        Exception exception = null;
        try {
            // 3. 执行后续 Filter 和 Controller
            if (wrappedRequest != null) {
                chain.doFilter(wrappedRequest, wrappedResponse);
            } else {
                chain.doFilter(request, wrappedResponse);
            }
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            // 4. 统一打印请求和响应日志
            long cost = System.currentTimeMillis() - startAt;
            logRequestAndResponse(
                    wrappedRequest != null ? wrappedRequest : httpRequest,
                    wrappedResponse,
                    exception,
                    cost
            );
            // 5. 确保响应内容被写入客户端
            wrappedResponse.copyBodyToResponse();
            // 6. 清理 TraceId
            TraceIdHolder.clear();
            MDC.remove(TraceIdHolder.MDC_KEY);
        }
    }

    /**
     * 统一打印请求和响应日志
     */
    private void logRequestAndResponse(HttpServletRequest request, ContentCachingResponseWrapper response,
                                       Exception ex, long cost) {
        if (!LOGGER.isInfoEnabled() && ex == null) {
            return;
        }
        if (!LOGGER.isErrorEnabled() && ex != null) {
            return;
        }

        try {
            // 提取请求信息
            String pathParams = extractPathParams(request);
            String queryParams = extractQueryParams(request);
            RequestBodyInfo requestBodyInfo = extractRequestBody(request);

            // 提取响应信息
            ResponseBodyInfo responseBodyInfo = extractResponseBody(response);

            int status = response != null ? response.getStatus() : (ex != null ? 500 : 200);

            if (ex != null) {
                // 异常情况：使用 ERROR 级别
                LOGGER.error("type=request-in||ip={}||method={}||uri={}||status={}||code={}||cost={}||pathParams={}||queryParams={}||requestBody={}||responseBody={}",
                        resolveClientIp(request),
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        responseBodyInfo.code,
                        cost,
                        pathParams,
                        queryParams,
                        requestBodyInfo.body,
                        responseBodyInfo.body,
                        ex);
            } else {
                // 正常情况：使用 INFO 级别
                LOGGER.info("type=request-in||ip={}||method={}||uri={}||status={}||code={}||cost={}||pathParams={}||queryParams={}||requestBody={}||responseBody={}",
                        resolveClientIp(request),
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        responseBodyInfo.code,
                        cost,
                        pathParams,
                        queryParams,
                        requestBodyInfo.body,
                        responseBodyInfo.body
                );
            }
        } catch (Exception loggingError) {
            LOGGER.debug("记录请求响应日志失败: {}", loggingError.getMessage(), loggingError);
        }
    }

    /**
     * 解析 TraceId（从请求头获取或生成新的）
     */
    private String resolveTraceId(HttpServletRequest request) {
        for (String header : TRACE_HEADER_CANDIDATES) {
            String value = request.getHeader(header);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 解析客户端IP地址
     */
    private String resolveClientIp(HttpServletRequest request) {
        String[] headerCandidates = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };
        for (String header : headerCandidates) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty()) {
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 安全提取路径参数
     * 从 request 属性中获取 Spring MVC 解析的路径变量
     */
    private String extractPathParams(HttpServletRequest request) {
        try {
            // Spring MVC 会在 HandlerMapping 阶段将路径变量存储在 request 属性中
            Object value = request.getAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
            if (!(value instanceof Map<?, ?> uriTemplateVars) || uriTemplateVars.isEmpty()) {
                return "";
            }

            // 转换为 Map<String, String>
            Map<String, String> pathParams = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : uriTemplateVars.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String val = entry.getValue() != null ? String.valueOf(entry.getValue()) : "";
                pathParams.put(key, val);
            }

            if (pathParams.isEmpty()) {
                return "";
            }

            // 转换为 JSON 字符串
            return truncate(safeToJson(pathParams));
        } catch (Exception ex) {
            LOGGER.debug("提取路径参数失败: {}", ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * 安全提取请求参数（Query Parameters）
     */
    private String extractQueryParams(HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap.isEmpty()) {
                return "";
            }

            // 转换为 Map<String, String>，数组值用逗号连接
            Map<String, String> queryParams = new LinkedHashMap<>();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    if (values.length == 1) {
                        queryParams.put(key, values[0]);
                    } else {
                        queryParams.put(key, String.join(",", values));
                    }
                }
            }

            if (queryParams.isEmpty()) {
                return "";
            }

            // 转换为 JSON 字符串
            return truncate(safeToJson(queryParams));
        } catch (Exception ex) {
            LOGGER.debug("提取请求参数失败: {}", ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * 请求体信息
     */
    private static class RequestBodyInfo {
        String body;

        RequestBodyInfo(String body) {
            this.body = body;
        }
    }

    /**
     * 响应体信息
     */
    private static class ResponseBodyInfo {
        String body;
        String code;

        ResponseBodyInfo(String body, String code) {
            this.body = body;
            this.code = code != null ? code : "";
        }
    }

    /**
     * 安全提取请求体
     * 只处理 POST、PUT、PATCH 请求，且需要是 ContentCachingRequestWrapper
     */
    private RequestBodyInfo extractRequestBody(HttpServletRequest request) {
        try {
            String method = request.getMethod();
            // 只处理有请求体的方法
            if (!"POST".equals(method) && !"PUT".equals(method) && !"PATCH".equals(method)) {
                return new RequestBodyInfo("");
            }

            // 检查是否是 ContentCachingRequestWrapper
            if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
                return new RequestBodyInfo("");
            }

            // ContentCachingRequestWrapper 只有在请求体被读取后才会缓存
            // 如果缓存为空，尝试读取一次以触发缓存
            byte[] contentAsByteArray = wrapper.getContentAsByteArray();
            if (contentAsByteArray == null || contentAsByteArray.length == 0) {
                // 尝试读取请求体以触发缓存
                try {
                    // 读取输入流会触发 ContentCachingRequestWrapper 的缓存机制
                    try (var inputStream = wrapper.getInputStream()) {
                        // 读取所有字节以触发缓存
                        inputStream.readAllBytes();
                    }
                    // 重新获取缓存的内容
                    contentAsByteArray = wrapper.getContentAsByteArray();
                } catch (IOException ioEx) {
                    // 读取失败，可能是流已关闭或其他原因，返回空字符串
                    LOGGER.debug("读取请求体失败: {}", ioEx.getMessage());
                    return new RequestBodyInfo("");
                }

                // 如果读取后仍然为空，返回空字符串
                if (contentAsByteArray == null || contentAsByteArray.length == 0) {
                    return new RequestBodyInfo("");
                }
            }

            // 检查 Content-Type，只处理 JSON 和表单数据
            String contentType = wrapper.getContentType();
            if (contentType == null) {
                return new RequestBodyInfo("");
            }

            String body;
            if (contentType.contains("application/json") || contentType.contains("application/xml")) {
                // JSON 或 XML 格式，直接转换为字符串
                body = new String(contentAsByteArray, StandardCharsets.UTF_8);
            } else if (contentType.contains("application/x-www-form-urlencoded")) {
                // 表单数据，转换为 Map
                String formData = new String(contentAsByteArray, StandardCharsets.UTF_8);
                Map<String, String> formParams = parseFormData(formData);
                body = safeToJson(formParams);
            } else {
                // 其他格式，只记录长度，不记录内容（避免二进制数据污染日志）
                return new RequestBodyInfo(String.format("{\"contentType\":\"%s\",\"size\":%d}", contentType, contentAsByteArray.length));
            }

            // 截断过长的内容
            return new RequestBodyInfo(truncate(body));
        } catch (Exception ex) {
            LOGGER.debug("提取请求体失败: {}", ex.getMessage(), ex);
            return new RequestBodyInfo("");
        }
    }

    /**
     * 安全提取响应体
     * 从 ContentCachingResponseWrapper 中提取响应内容
     * 如果响应体是 JSON 且是 ApiResponse 格式，提取 code 字段
     */
    private ResponseBodyInfo extractResponseBody(ContentCachingResponseWrapper response) {
        try {
            if (response == null) {
                return new ResponseBodyInfo("", "");
            }

            // 获取缓存的响应体内容
            byte[] contentAsByteArray = response.getContentAsByteArray();
            if (contentAsByteArray == null || contentAsByteArray.length == 0) {
                return new ResponseBodyInfo("", "");
            }

            // 检查 Content-Type，只处理 JSON 和文本类型
            String contentType = response.getContentType();
            if (contentType == null) {
                return new ResponseBodyInfo("", "");
            }

            String body;
            String code = "";
            if (contentType.contains("application/json") ||
                    contentType.contains("application/xml") ||
                    contentType.contains("text/")) {
                // JSON、XML 或文本格式，直接转换为字符串
                body = new String(contentAsByteArray, StandardCharsets.UTF_8);

                // 如果是 JSON 格式，尝试提取 ApiResponse 的 code 字段
                if (contentType.contains("application/json") && StringUtils.hasText(body)) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(body);
                        if (jsonObject != null && jsonObject.containsKey("code")) {
                            Object codeObj = jsonObject.get("code");
                            if (codeObj != null) {
                                code = String.valueOf(codeObj);
                            }
                        }
                    } catch (Exception jsonEx) {
                        // JSON 解析失败，忽略，code 保持为空
                        LOGGER.debug("解析响应体 JSON 失败: {}", jsonEx.getMessage());
                    }
                }
            } else {
                // 其他格式，只记录长度，不记录内容（避免二进制数据污染日志）
                return new ResponseBodyInfo(
                        String.format("{\"contentType\":\"%s\",\"size\":%d}", contentType, contentAsByteArray.length),
                        ""
                );
            }

            // 截断过长的内容
            return new ResponseBodyInfo(truncate(body), code);
        } catch (Exception ex) {
            LOGGER.debug("提取响应体失败: {}", ex.getMessage(), ex);
            return new ResponseBodyInfo("", "");
        }
    }

    /**
     * 解析表单数据
     */
    private Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new LinkedHashMap<>();
        if (!StringUtils.hasText(formData)) {
            return params;
        }

        try {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = decodeUrl(keyValue[0]);
                    String value = decodeUrl(keyValue[1]);
                    params.put(key, value);
                } else if (keyValue.length == 1) {
                    params.put(decodeUrl(keyValue[0]), "");
                }
            }
        } catch (Exception ex) {
            LOGGER.debug("解析表单数据失败: {}", ex.getMessage(), ex);
        }

        return params;
    }

    /**
     * URL 解码
     */
    private String decodeUrl(String encoded) {
        try {
            return java.net.URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return encoded;
        }
    }

    /**
     * 安全地将对象转换为JSON字符串
     */
    private String safeToJson(Object object) {
        if (object == null) {
            return "null";
        }
        try {
            return JSON.toJSONString(object);
        } catch (Exception ex) {
            return String.valueOf(object);
        }
    }

    /**
     * 截断过长的字符串
     */
    private String truncate(String source) {
        if (!StringUtils.hasText(source)) {
            return "";
        }
        if (source.length() <= MAX_LOG_LENGTH) {
            return source;
        }
        return source.substring(0, MAX_LOG_LENGTH) + "...";
    }
}
