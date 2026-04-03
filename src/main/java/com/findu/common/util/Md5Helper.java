package com.findu.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 工具类，用于计算对象的 MD5 值。
 */
public final class Md5Helper {

    private Md5Helper() {
    }

    /**
     * 计算对象内容的 MD5 值。
     * 将对象序列化为 JSON 字符串后计算 MD5，用于防重复提交等场景。
     *
     * @param object 待计算的对象
     * @return MD5 值（32位十六进制字符串），如果计算失败则返回当前时间戳
     */
    public static String calculateMd5(Object object) {
        try {
            // 将对象序列化为 JSON 字符串（使用 FastJsonHelper 保持代码风格一致）
            String json = FastJsonHelper.toJson(object);

            // 计算 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = null;
            if (json != null) {
                digest = md.digest(json.getBytes(StandardCharsets.UTF_8));
            }

            // 转换为十六进制字符串
            StringBuilder sb = new StringBuilder(32);
            if (digest != null) {
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            // 如果计算 MD5 失败，使用时间戳作为 fallback，确保不会因为 MD5 计算失败而阻塞业务
            return String.valueOf(System.currentTimeMillis());
        }
    }
}


