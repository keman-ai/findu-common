package com.findu.common.moderation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 审核提交请求。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationSubmitRequest {

    @JsonProperty("material_id")
    private String materialId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("text_content")
    private String textContent;

    @JsonProperty("material_url")
    private String materialUrl;

    @JsonProperty("callback_url")
    private String callbackUrl;

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getMaterialUrl() {
        return materialUrl;
    }

    public void setMaterialUrl(String materialUrl) {
        this.materialUrl = materialUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}

