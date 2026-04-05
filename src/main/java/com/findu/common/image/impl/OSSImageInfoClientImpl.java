package com.findu.common.image.impl;

import com.findu.common.image.ImageInfoClient;
import com.findu.common.util.FastJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * OSS图片信息客户端实现。
 * 使用阿里云OSS的图片信息API获取图片宽高。
 */
public class OSSImageInfoClientImpl implements ImageInfoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSSImageInfoClientImpl.class);

    private final RestTemplate restTemplate;

    public OSSImageInfoClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> getImageInfo(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // 判断是否是OSS URL
        if (!isOssUrl(imageUrl)) {
            LOGGER.warn("非OSS URL，无法获取图片信息，imageUrl={}", imageUrl);
            return null;
        }

        try {
            // 构建OSS图片信息查询URL
            String infoUrl = buildOssInfoUrl(imageUrl);
            
            // 调用OSS API获取图片信息
            String response = restTemplate.getForObject(infoUrl, String.class);
            
            if (!StringUtils.hasText(response)) {
                LOGGER.warn("OSS返回空响应，imageUrl={}", imageUrl);
                return null;
            }

            // 解析JSON响应
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonObject = FastJsonHelper.parseObject(response, Map.class);
            
            if (jsonObject == null) {
                LOGGER.warn("OSS响应解析失败，imageUrl={}, response={}", imageUrl, response);
                return null;
            }
            
            // 提取ImageWidth和ImageHeight
            Object widthObjObj = jsonObject.get("ImageWidth");
            Object heightObjObj = jsonObject.get("ImageHeight");
            
            if (widthObjObj == null || heightObjObj == null) {
                LOGGER.warn("OSS响应中缺少宽高信息，imageUrl={}, response={}", imageUrl, response);
                return null;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> widthObj = (Map<String, Object>) widthObjObj;
            @SuppressWarnings("unchecked")
            Map<String, Object> heightObj = (Map<String, Object>) heightObjObj;
            
            String widthStr = (String) widthObj.get("value");
            String heightStr = (String) heightObj.get("value");
            
            if (!StringUtils.hasText(widthStr) || !StringUtils.hasText(heightStr)) {
                LOGGER.warn("OSS响应中宽高值为空，imageUrl={}, response={}", imageUrl, response);
                return null;
            }

            int width = Integer.parseInt(widthStr);
            int height = Integer.parseInt(heightStr);

            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("url", imageUrl);
            imageInfo.put("width", width);
            imageInfo.put("height", height);

            return imageInfo;
        } catch (NumberFormatException e) {
            LOGGER.warn("解析OSS响应中的宽高值失败，imageUrl={}, error={}", imageUrl, e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.warn("获取OSS图片信息失败，imageUrl={}, error={}", imageUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否是OSS URL。
     *
     * @param url 图片URL
     * @return 是否是OSS URL
     */
    private boolean isOssUrl(String url) {
        return url != null && (url.contains(".oss-") || url.contains(".oss."));
    }

    /**
     * 构建OSS图片信息查询URL。
     * 格式：原URL + ?x-oss-process=image/info
     *
     * @param imageUrl 原始图片URL
     * @return 图片信息查询URL
     */
    private String buildOssInfoUrl(String imageUrl) {
        // 如果URL已经包含查询参数，使用&连接，否则使用?连接
        if (imageUrl.contains("?")) {
            return imageUrl + "&x-oss-process=image/info";
        } else {
            return imageUrl + "?x-oss-process=image/info";
        }
    }
}

