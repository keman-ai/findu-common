package com.findu.common.model;

import java.io.Serializable;

/**
 * 用户能力项实体，用于描述用户的能力信息。
 *
 * <p>能力数据会以 JSON 数组形式存储在数据库中，格式为：[{"text":""}]</p>
 */
public class AbilityItem implements Serializable {

    /**
     * 能力描述文本。
     */
    private String text;

    /**
     * 默认构造函数，便于序列化与反射创建对象。
     */
    public AbilityItem() {
    }

    /**
     * 带参构造函数，用于快速创建能力项对象。
     *
     * @param text 能力描述文本
     */
    public AbilityItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

