package com.findu.common.dto;

/**
 * 用户扩展信息传输对象，用于对外展示统计或扩展字段。
 */
public class UserExtraInfoDTO {

    /**
     * 扩展信息键。
     */
    private String key;

    /**
     * 扩展信息值。
     */
    private String value;

    public UserExtraInfoDTO() {
    }

    public UserExtraInfoDTO(String key, String value) {
        this.key = key;
        this.value = value;
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
}

