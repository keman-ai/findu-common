package com.findu.common.exception;

import java.util.Map;

public enum CommonErrorCode implements ErrorCode {

    PARAM_MISSING("PARAM_MISSING",
        "Required parameter is missing",
        "Include the '{field}' field in your request body. It must not be null or empty."),

    PARAM_INVALID("PARAM_INVALID",
        "Parameter validation failed",
        "Check the '{field}' field. Constraint: {constraint}. Your value: {actual}."),

    UNAUTHORIZED("UNAUTHORIZED",
        "Authentication required",
        "Provide a valid token in the Authorization header or ensure X-User-ID header is set by gateway."),

    FORBIDDEN("FORBIDDEN",
        "Access denied",
        "You don't have permission for this resource. Verify the userId matches the resource owner."),

    NOT_FOUND("NOT_FOUND",
        "Resource not found",
        "The requested {resource} with id '{id}' does not exist. Verify the ID and try again."),

    CONFLICT("CONFLICT",
        "State conflict",
        "The {resource} is in '{currentState}' state, which does not allow '{action}'. Allowed actions: {allowedActions}."),

    RATE_LIMITED("RATE_LIMITED",
        "Too many requests",
        "You've exceeded the rate limit. Wait {retryAfter} seconds before retrying."),

    INTERNAL_ERROR("INTERNAL_ERROR",
        "Internal server error",
        "This is a server-side issue. Retry the request. If it persists, contact support with traceId: {traceId}.");

    private final String code;
    private final String message;
    private final String howto;

    CommonErrorCode(String code, String message, String howto) {
        this.code = code;
        this.message = message;
        this.howto = howto;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getHowto() { return howto; }

    /**
     * Fill howto template with context values.
     * Replaces {key} placeholders with values from the context map.
     */
    public static String fillHowto(String template, Map<String, Object> context) {
        if (template == null || context == null || context.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
