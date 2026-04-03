package com.findu.common.extend;

import java.util.List;
import java.util.Map;

/**
 * 扩展信息处理器接口
 * 每个type对应一个实现类，负责该类型扩展信息的所有处理逻辑
 */
public interface ExtendInfoProcessor {
    
    /**
     * 获取支持的类型列表
     * 
     * @return 支持的内容类型列表
     */
    List<Integer> getSupportedTypes();
    
    /**
     * 验证扩展信息
     * 
     * @param extendInfo 扩展信息Map
     * @throws IllegalArgumentException 验证失败时抛出
     */
    void validate(Map<String, Object> extendInfo);
    
    /**
     * 将Map转换为领域对象
     * 
     * @param extendInfoMap 扩展信息Map
     * @return 领域对象
     */
    Object convertToDomain(Map<String, Object> extendInfoMap);
    
    /**
     * 将领域对象转换为Map
     * 
     * @param extendInfoObj 领域对象
     * @return 扩展信息Map
     */
    Map<String, Object> convertToMap(Object extendInfoObj);
    
    /**
     * 将JSON字符串转换为领域对象
     * 
     * @param extendInfoJson 扩展信息JSON字符串
     * @return 领域对象
     */
    Object parseFromJson(String extendInfoJson);
    
    /**
     * 将领域对象转换为JSON字符串
     * 
     * @param extendInfoObj 领域对象
     * @return JSON字符串
     */
    String toJson(Object extendInfoObj);
    
    /**
     * 构建审核材料列表
     * 
     * @param changeId 变更申请ID
     * @param extendInfoObj 扩展信息领域对象
     * @return 审核材料列表
     */
    List<ModerationMaterial> buildModerationMaterials(Long changeId, Object extendInfoObj);
    
    /**
     * 解析审核结果中的扩展信息审核状态（下划线命名，用于存储到 auditResult）
     * 
     * @param changeId 变更申请ID
     * @param reviewDetails 审核详情列表
     * @param extendInfoObj 扩展信息领域对象
     * @return 扩展信息审核结果Map（下划线命名）
     */
    Map<String, Object> parseAuditResult(Long changeId, List<Map<String, Object>> reviewDetails, Object extendInfoObj);
    
    /**
     * 解析审核结果中的扩展信息审核状态（驼峰命名，用于返回给前端）
     * 直接从 auditResult 中提取 extendInfo 相关的审核结果
     * 
     * @param changeId 变更申请ID
     * @param auditResult 审核结果Map（包含下划线命名的审核结果）
     * @return 扩展信息审核结果Map（驼峰命名）
     */
    Map<String, Object> parseAuditResultForDTO(Long changeId, Map<String, Object> auditResult);
    
    /**
     * 审核材料项
     */
    class ModerationMaterial {
        private String materialId;
        private String contentType; // "text" | "image" | "video"
        private String textContent;
        private String materialUrl;
        
        public ModerationMaterial(String materialId, String contentType, String textContent) {
            this.materialId = materialId;
            this.contentType = contentType;
            this.textContent = textContent;
        }
        
        public ModerationMaterial(String materialId, String contentType, String materialUrl, boolean isUrl) {
            this.materialId = materialId;
            this.contentType = contentType;
            this.materialUrl = materialUrl;
        }
        
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

