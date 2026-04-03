package com.findu.common.moderation;

import java.util.List;

/**
 * 内容审核客户端。
 * 通用的审核接口，不包含业务领域概念。
 */
public interface ContentModerationClient {

    /**
     * 提交文本审核。
     *
     * @param materialId  材料ID
     * @param userId      用户ID
     * @param text        文本内容
     * @param callbackUrl 回调URL（由调用方提供）
     * @return 审核响应
     */
    ModerationResponse submitText(String materialId, String userId, String text, String callbackUrl);

    /**
     * 提交图片审核。
     *
     * @param materialId  材料ID
     * @param userId       用户ID
     * @param url          图片地址
     * @param callbackUrl 回调URL（由调用方提供）
     * @return 审核响应
     */
    ModerationResponse submitImage(String materialId, String userId, String url, String callbackUrl);

    /**
     * 提交混合内容审核（标题+正文+图片+视频+扩展信息）。
     * 通用的混合内容审核接口，不包含业务领域概念。
     *
     * @param materialId  材料ID（可以是变更申请ID或其他业务ID）
     * @param userId       用户ID
     * @param materials    审核材料列表
     * @param callbackUrl  回调URL（由调用方提供）
     * @return 审核响应
     */
    ModerationResponse submitMixed(String materialId, String userId, List<ModerationMaterial> materials, String callbackUrl);

    /**
     * 查询审核状态。
     *
     * @param reviewId 审核ID
     * @param userId   用户ID
     * @return 审核响应
     */
    ModerationResponse queryReviewStatus(String reviewId, String userId);

    /**
     * 审核材料项。
     */
    class ModerationMaterial {
        private String materialId;
        private String contentType; // "text", "image", "video"
        private String textContent;
        private String materialUrl;

        public ModerationMaterial(String materialId, String contentType) {
            this.materialId = materialId;
            this.contentType = contentType;
        }

        // Getters and Setters
        public String getMaterialId() { return materialId; }
        public void setMaterialId(String materialId) { this.materialId = materialId; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public String getTextContent() { return textContent; }
        public void setTextContent(String textContent) { this.textContent = textContent; }
        public String getMaterialUrl() { return materialUrl; }
        public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    }
}

