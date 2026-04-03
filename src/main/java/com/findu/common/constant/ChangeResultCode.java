package com.findu.common.constant;

/**
 * 变更结果错误码枚举。
 */
public enum ChangeResultCode {

    /**
     * 变更申请状态不在审核中。
     */
    CHANGE_STATUS_INVALID("CHANGE_STATUS_INVALID"),

    /**
     * 作品状态不允许变更。
     */
    WORKS_STATUS_INVALID("WORKS_STATUS_INVALID"),

    /**
     * 成功。
     */
    SUCCESS("");

    /**
     * 错误码。
     */
    private final String code;

    ChangeResultCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}





