package com.findu.common.util;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * FastJson 工具类，统一管理 JSON 序列化与反序列化逻辑。
 */
public final class FastJsonHelper {

    private FastJsonHelper() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param object 原始对象
     * @return JSON 字符串
     */
    public static String toJson(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        return JSON.toJSONString(object);
    }

    /**
     * 将 JSON 字符串解析为目标对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 解析结果
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将 JSON 数组解析为对象列表。
     *
     * @param json  JSON 数组字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return 对象列表
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        return JSON.parseArray(json, clazz);
    }
}

