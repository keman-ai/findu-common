package com.findu.common.exception;

/**
 * 系统业务错误码枚举，统一定义错误码与默认说明。
 */
public enum ErrorCode {

    /**
     * 未授权操作。
     */
    UNAUTHORIZED_OPERATION("401", "未授权的操作"),

    /**
     * 资源不存在。
     */
    DATA_NOT_FOUND("402", "数据不存在"),

    /**
     * 重复操作。
     */
    DUPLICATE_OPERATION("403", "重复操作"),

    /**
     * 参数校验失败。
     */
    VALIDATION_ERROR("404", "参数校验失败"),

    /**
     * 状态非法。
     */
    ILLEGAL_STATUS("405", "资源状态不允许该操作"),

    /**
     * 资源数量超过限制
     */
    TOO_MATCH("406", "资源数量超过限制"),

    /**
     * 缺少权限验证
     */
    AUTHORIZATION_LEAK("407", "缺少权限验证"),

    /**
     * 不支持的字段名称
     */
    ILLEGAL_PROFILE_KEY("408", "不支持的字段名称"),

    /**
     * 并发更新冲突
     */
    CONCURRENT_UPDATE("409", "数据已被其他操作更新，请刷新后重试"),

    PATH_NOT_EXISTS("410", "资源不存在"),

    /**
     * 未分类错误。
     */
    INTERNAL_ERROR("999", "系统内部错误");

    /**
     * 错误码。
     */
    private final String code;

    /**
     * 默认错误提示。
     */
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

