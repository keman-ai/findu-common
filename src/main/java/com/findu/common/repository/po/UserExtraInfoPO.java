package com.findu.common.repository.po;

/**
 * 用户扩展信息持久化对象，对应表 user_extra_info。
 */
public class UserExtraInfoPO {

    /**
     * 主键标识。
     */
    private Long id;

    /**
     * 用户标识。
     */
    private String userId;

    /**
     * 扩展信息键。
     */
    private String infoKey;

    /**
     * 扩展信息值。
     */
    private String infoValue;

    /**
     * 创建时间。
     */
    private String gmtCreate;

    /**
     * 更新时间。
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

    public String getInfoKey() {
        return infoKey;
    }

    public void setInfoKey(String infoKey) {
        this.infoKey = infoKey;
    }

    public String getInfoValue() {
        return infoValue;
    }

    public void setInfoValue(String infoValue) {
        this.infoValue = infoValue;
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

