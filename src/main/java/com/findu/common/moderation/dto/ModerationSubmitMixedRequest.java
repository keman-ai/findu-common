package com.findu.common.moderation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 混合内容审核提交请求。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationSubmitMixedRequest {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("materials")
    private List<MaterialItem> materials;

    @JsonProperty("callback_url")
    private String callbackUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<MaterialItem> getMaterials() {
        return materials;
    }

    public void setMaterials(List<MaterialItem> materials) {
        this.materials = materials;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    /**
     * 材料项。
     */
    public static class MaterialItem {
        @JsonProperty("material_id")
        private String materialId;

        @JsonProperty("content_type")
        private String contentType;

        @JsonProperty("text_content")
        private String textContent;

        @JsonProperty("material_url")
        private String materialUrl;

        public String getMaterialId() {
            return materialId;
        }

        public void setMaterialId(String materialId) {
            this.materialId = materialId;
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
    }
}

