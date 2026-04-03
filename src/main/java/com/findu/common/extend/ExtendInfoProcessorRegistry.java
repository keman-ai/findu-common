package com.findu.common.extend;

import com.findu.common.extend.impl.EmptyExtendInfoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展信息处理器注册表
 */
@Component
public class ExtendInfoProcessorRegistry {

    @Autowired(required = false)
    private List<ExtendInfoProcessor> processors;

    @Autowired
    private EmptyExtendInfoProcessor defaultProcessor;

    private Map<Integer, ExtendInfoProcessor> processorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (processors != null) {
            for (ExtendInfoProcessor processor : processors) {
                // 跳过默认处理器，避免重复注册
                if (processor instanceof EmptyExtendInfoProcessor) {
                    continue;
                }

                List<Integer> supportedTypes = processor.getSupportedTypes();
                if (supportedTypes != null) {
                    for (Integer type : supportedTypes) {
                        if (type != null) {
                            // 如果已存在处理器，记录警告（避免冲突）
                            if (processorMap.containsKey(type)) {
                                throw new IllegalStateException(
                                        String.format("类型%d存在多个处理器，请检查配置", type));
                            }
                            processorMap.put(type, processor);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取指定类型的处理器
     * 如果没有匹配的处理器，返回默认的EmptyExtendInfoProcessor
     *
     * @param type 内容类型
     * @return 处理器，永远不会返回null
     */
    public ExtendInfoProcessor getProcessor(Integer type) {
        if (type == null) {
            return defaultProcessor;
        }

        ExtendInfoProcessor processor = processorMap.get(type);
        if (processor == null) {
            // 没有匹配的处理器，返回默认处理器
            return defaultProcessor;
        }

        return processor;
    }

    /**
     * 检查指定类型是否有专门的处理器（非默认处理器）
     *
     * @param type 内容类型
     * @return 是否存在专门的处理器
     */
    public boolean hasSpecificProcessor(Integer type) {
        if (type == null) {
            return false;
        }
        return processorMap.containsKey(type);
    }

    /**
     * 获取所有已注册的类型（不包括默认处理器处理的类型）
     *
     * @return 已注册的类型列表
     */
    public List<Integer> getAllRegisteredTypes() {
        return new ArrayList<>(processorMap.keySet());
    }
}

