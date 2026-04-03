package com.findu.common.mq.impl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AWS SNS 配置类。
 *
 * @author system
 */
@Configuration
@EnableConfigurationProperties(SNSProperties.class)
public class SNSConfiguration {
}
