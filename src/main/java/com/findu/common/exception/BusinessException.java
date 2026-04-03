package com.findu.common.exception;

/**
 * 业务异常类，用于在流程中抛出业务错误。
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码。
     */
    private final ErrorCode errorCode;

    /**
     * 构造异常。
     *
     * @param errorCode 错误码
     * @param message   错误描述
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造异常。
     *
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

