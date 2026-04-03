package com.findu.common.constant;

/**
 * 审核来源枚举。
 */
public enum AuditSource {

    /**
     * 内部 MIS 平台人工审核。
     */
    MIS(0),

    /**
     * 外部审核服务。
     */
    EXTERNAL(1);

    private final int code;

    AuditSource(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

