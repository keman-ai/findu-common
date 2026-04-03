package com.findu.common.constant;

/**
 * 审核状态枚举，适用于资料变更与展示材料。
 */
public enum AuditStatus {

    /**
     * 待审核。
     */
    PENDING(0),

    /**
     * 审核通过。
     */
    APPROVED(1),

    /**
     * 审核拒绝。
     */
    REJECTED(2);

    /**
     * 枚举值。
     */
    private final int code;

    AuditStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

