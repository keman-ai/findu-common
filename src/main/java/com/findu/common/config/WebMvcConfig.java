package com.findu.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置。
 * 
 * 注意：请求和响应日志记录已改为使用 {@link com.findu.common.trace.RequestLoggingFilter}，
 * 不再需要注册拦截器。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 请求和响应日志记录已改为使用 RequestLoggingFilter，不再需要拦截器
}



