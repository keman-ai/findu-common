package com.findu.common.mq.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS SNS 配置属性。
 *
 * @author system
 */
@ConfigurationProperties(prefix = "aws.sns")
public class SNSProperties {

    /**
     * AWS 区域。
     */
    private String region = "ap-southeast-1";

    /**
     * 默认 SNS Topic ARN。
     * 格式: arn:aws:sns:{region}:{accountId}:{topicName}
     */
    private String topicArn;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTopicArn() {
        return topicArn;
    }

    public void setTopicArn(String topicArn) {
        this.topicArn = topicArn;
    }
}
