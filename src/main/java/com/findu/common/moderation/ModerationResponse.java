package com.findu.common.moderation;

/**
 * 内容审核响应结果。
 */
public class ModerationResponse {

    private final String reviewId;
    private final String reviewStatus;
    private final String riskLevel;
    private final String rejectReason;

    public ModerationResponse(String reviewId, String reviewStatus, String riskLevel, String rejectReason) {
        this.reviewId = reviewId;
        this.reviewStatus = reviewStatus;
        this.riskLevel = riskLevel;
        this.rejectReason = rejectReason;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * 构建默认的待审核响应，用于降级。
     *
     * @return 待审核响应
     */
    public static ModerationResponse pendingFallback() {
        return new ModerationResponse("fallback-" + java.util.UUID.randomUUID(), "pending", "medium", "");
    }

    /**
     * 构建跳过审核的响应，默认状态为 APPROVED。
     * auditId 格式：skip-UUID（去掉-）
     *
     * @return 跳过审核的响应
     */
    public static ModerationResponse skipModeration() {
        String auditId = "skip-" + java.util.UUID.randomUUID().toString().replace("-", "");
        return new ModerationResponse(auditId, "approved", "none", "");
    }
}

