package com.findu.common.exception;

import java.util.Map;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> context;

    public BusinessException(ErrorCode errorCode, Map<String, Object> context) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.context = context;
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, Map.of());
    }

    /**
     * Legacy constructor for backward compatibility.
     * Maps to INTERNAL_ERROR with the message as howto.
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = CommonErrorCode.INTERNAL_ERROR;
        this.context = Map.of("detail", message);
    }

    /**
     * Legacy constructor: ErrorCode + custom message.
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = Map.of("detail", message);
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public Map<String, Object> getContext() { return context; }
}
