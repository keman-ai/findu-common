package com.findu.common.image;

import java.util.Map;

/**
 * 图片信息客户端接口。
 * 用于获取图片的宽高信息。
 */
public interface ImageInfoClient {

    /**
     * 获取图片信息（宽、高）。
     *
     * @param imageUrl 图片URL
     * @return 包含url、width、height的Map，如果获取失败则返回null
     */
    Map<String, Object> getImageInfo(String imageUrl);
}

