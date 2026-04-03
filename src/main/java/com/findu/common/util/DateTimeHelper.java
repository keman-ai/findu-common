package com.findu.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类，用于格式化时间为 MySQL DATETIME 格式。
 */
public final class DateTimeHelper {

    /**
     * MySQL DATETIME 格式：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter MYSQL_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter STANDARD_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");



    private DateTimeHelper() {
    }

    /**
     * 获取当前 UTC 时间，格式化为 MySQL DATETIME 格式字符串。
     *
     * @return MySQL DATETIME 格式的时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String now() {
        return LocalDateTime.now().format(MYSQL_DATETIME_FORMATTER);
    }

    public static String timestamp() {
        return LocalDateTime.now().format(STANDARD_DATETIME_FORMATTER);
    }
}

