package com.findu.common.moderation;

import java.util.List;
import java.util.Map;

/**
 * 审核处理器接口。
 * 不同业务领域的审核处理器封装各自的审核规则：
 * - 审核材料构建（哪些字段需要审核）
 * - 回调URL构建
 * - 审核结果解析（将ModerationResponse映射到业务状态）
 * - 审核回调结果解析（将reviewDetails解析为auditResult）
 * 
 * @param <T> 审核请求上下文类型
 * @param <R> 审核结果类型
 */
public interface ModerationHandler<T, R> {

    /**
     * 构建审核材料列表。
     * 
     * @param context 审核请求上下文
     * @return 审核材料列表
     */
    List<ModerationMaterial> buildMaterials(T context);

    /**
     * 构建回调URL。
     * 
     * @param callbackBaseUrl 回调基础URL（从配置读取）
     * @return 完整的回调URL，如果不需要回调则返回null
     */
    String buildCallbackUrl(String callbackBaseUrl);

    /**
     * 解析审核回调结果，将reviewDetails解析为auditResult Map。
     * 用于处理审核平台回调时的详细审核结果。
     * 
     * @param reviewId 审核ID
     * @param reviewDetails 审核详情列表（包含每个material的审核结果）
     * @param callbackContext 回调上下文，包含解析审核结果所需的信息（如snapshotAfter等）
     * @return 审核结果Map，包含各个字段的审核状态和备注
     */
    Map<String, Object> parseAuditResult(String reviewId, List<Map<String, Object>> reviewDetails, Map<String, Object> callbackContext);

    /**
     * 提交审核。
     * 
     * @param context 审核请求上下文
     * @return 审核响应
     */
    ModerationResponse submit(T context);

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
