package com.findu.common.constant;

/**
 * 删除标记枚举。
 */
public enum DeleteFlag {

    /**
     * 未删除。
     */
    NOT_DELETED(0),

    /**
     * 已删除。
     */
    DELETED(1);

    /**
     * 枚举值。
     */
    private final int code;

    DeleteFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

