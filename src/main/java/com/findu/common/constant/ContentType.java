package com.findu.common.constant;

/**
 * 用户作品内容类型枚举。
 */
public enum ContentType {

    /**
     * 图片+文案。
     */
    IMAGE_TEXT(1),

    /**
     * 视频+文案。
     */
    VIDEO_TEXT(2);

    /**
     * 枚举值。
     */
    private final int code;

    ContentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}





