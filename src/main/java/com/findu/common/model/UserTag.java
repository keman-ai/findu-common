package com.findu.common.model;

import java.io.Serializable;

/**
 * 用户标签聚合中的标签实体，描述用户在平台上的特定标签信息。
 *
 * <p>标签数据会以 JSON 形式存储在数据库中，通过 FastJson 序列化与反序列化。</p>
 */
public class UserTag implements Serializable {

    /**
     * 标签名称，例如“资深设计师”。
     */
    private String name;

    /**
     * 标签类型，用于区分系统标签、自定义标签等业务类别。
     */
    private String type;

    /**
     * 默认构造函数，便于序列化与反射创建对象。
     */
    public UserTag() {
    }

    /**
     * 带参构造函数，用于快速创建标签对象。
     *
     * @param name 标签名称
     * @param type 标签类型
     */
    public UserTag(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

