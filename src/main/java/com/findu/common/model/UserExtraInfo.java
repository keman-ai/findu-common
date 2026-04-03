package com.findu.common.model;

import java.io.Serializable;

/**
 * 用户扩展信息实体，用于保存非标准结构化的用户统计或扩展数据。
 *
 * <p>该实体与 {@code user_extra_info} 表一一对应，通过键值对存储扩展字段。</p>
 */
public class UserExtraInfo implements Serializable {

    /**
     * 扩展信息的数据库主键标识。
     */
    private Long id;

    /**
     * 对应的用户唯一标识，关联用户主表。
     */
    private String userId;

    /**
     * 扩展信息的键，定义信息类型，例如“completedOrders”。
     */
    private String key;

    /**
     * 扩展信息的值，存储实际的统计或描述数据。
     */
    private String value;

    /**
     * 记录创建时间，采用 ISO8601 字符串表示。
     */
    private String gmtCreate;

    /**
     * 记录最近更新时间，采用 ISO8601 字符串表示。
     */
    private String gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }
}

