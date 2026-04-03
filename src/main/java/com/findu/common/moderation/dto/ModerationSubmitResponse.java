package com.findu.common.moderation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 审核提交响应数据。
 */
public class ModerationSubmitResponse {

    @JsonProperty("review_id")
    private String reviewId;

    @JsonProperty("review_status")
    private String reviewStatus;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("reject_reason")
    private String rejectReason;

    @JsonProperty("alibaba_task_id")
    private String alibabaTaskId;

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getAlibabaTaskId() {
        return alibabaTaskId;
    }

    public void setAlibabaTaskId(String alibabaTaskId) {
        this.alibabaTaskId = alibabaTaskId;
    }
}

