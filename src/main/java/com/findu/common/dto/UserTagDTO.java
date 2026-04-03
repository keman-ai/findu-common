package com.findu.common.dto;

/**
 * 用户标签传输对象，描述标签名称与类别。
 */
public class UserTagDTO {

    /**
     * 标签名称。
     */
    private String name;

    /**
     * 标签类型。
     */
    private String type;

    public UserTagDTO() {
    }

    public UserTagDTO(String name, String type) {
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

