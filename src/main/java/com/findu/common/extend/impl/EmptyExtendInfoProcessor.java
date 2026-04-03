package com.findu.common.extend.impl;

import com.findu.common.extend.ExtendInfoProcessor;
import com.findu.common.exception.BusinessException;
import com.findu.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 空扩展信息处理器（默认处理器）
 * 用于type=1，以及所有未注册的类型
 * 这些类型不需要扩展信息，但保持统一的处理流程
 * 注意：type=2和type=3使用MyServiceExtendInfoProcessor处理扩展信息
 */
@Component
public class EmptyExtendInfoProcessor implements ExtendInfoProcessor {
    
    /**
     * 支持的类型：1-图片+文案
     * 同时也作为所有未注册类型的默认处理器
     */
    private static final List<Integer> SUPPORTED_TYPES = List.of(1);
    
    @Override
    public List<Integer> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public void validate(Map<String, Object> extendInfo) {

    }
    
    @Override
    public Object convertToDomain(Map<String, Object> extendInfoMap) {
        // 返回null，表示没有领域对象
        return null;
    }
    
    @Override
    public Map<String, Object> convertToMap(Object extendInfoObj) {
        // 返回null，表示没有Map结构
        return null;
    }
    
    @Override
    public Object parseFromJson(String extendInfoJson) {
        // 即使有JSON字符串（空字符串），也返回null
        return null;
    }
    
    @Override
    public String toJson(Object extendInfoObj) {
        // 返回空字符串，符合数据库默认值
        return "";
    }
    
    @Override
    public List<ModerationMaterial> buildModerationMaterials(Long changeId, Object extendInfoObj) {
        // 不构建任何审核材料
        return new ArrayList<>();
    }
    
    @Override
    public Map<String, Object> parseAuditResult(Long changeId, List<Map<String, Object>> reviewDetails, Object extendInfoObj) {
        // 返回空的审核结果
        return new HashMap<>();
    }
    
    @Override
    public Map<String, Object> parseAuditResultForDTO(Long changeId, Map<String, Object> auditResult) {
        // 返回空的审核结果
        return new HashMap<>();
    }
}

