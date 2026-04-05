package com.findu.common.mq.impl;

import com.findu.common.mq.MQProducer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * AWS SNS 消息生产者实现（生产环境）。
 * 替代 RocketMQ，使用 AWS SNS 发送事件消息。
 * 凭证通过 IAM Role / 环境变量 / ~/.aws/credentials 自动获取，无需硬编码。
 *
 * @author system
 */
public class SNSProducer implements MQProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SNSProducer.class);

    private final SNSProperties properties;
    private SnsClient snsClient;

    public SNSProducer(SNSProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(properties.getTopicArn())) {
            LOGGER.warn("SNS topic ARN is not configured, SNSProducer will be disabled");
            return;
        }
        try {
            snsClient = SnsClient.builder()
                    .region(Region.of(properties.getRegion()))
                    .build();
            LOGGER.info("SNS Producer initialized, region={}, topicArn={}",
                    properties.getRegion(), properties.getTopicArn());
        } catch (Exception e) {
            LOGGER.error("Failed to initialize SNS Producer", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (snsClient != null) {
            snsClient.close();
            LOGGER.info("SNS Producer closed");
        }
    }

    @Override
    public void sendMessage(String topic, String tag, String key, Object payload) {
        if (snsClient == null) {
            LOGGER.warn("SNS Producer is not initialized, skip sending message");
            return;
        }
        try {
            EventMessage eventMessage = new EventMessage();
            eventMessage.setEventType(tag);
            eventMessage.setPayload(payload);
            String messageBody = com.alibaba.fastjson.JSON.toJSONString(eventMessage);

            Map<String, MessageAttributeValue> attributes = new HashMap<>();
            attributes.put("eventType", MessageAttributeValue.builder()
                    .dataType("String").stringValue(tag).build());
            if (StringUtils.hasText(key)) {
                attributes.put("messageKey", MessageAttributeValue.builder()
                        .dataType("String").stringValue(key).build());
            }
            if (StringUtils.hasText(topic)) {
                attributes.put("originalTopic", MessageAttributeValue.builder()
                        .dataType("String").stringValue(topic).build());
            }

            PublishRequest request = PublishRequest.builder()
                    .topicArn(properties.getTopicArn())
                    .message(messageBody)
                    .messageAttributes(attributes)
                    .build();

            PublishResponse response = snsClient.publish(request);
            LOGGER.info("Successfully sent message to SNS, topic={}, tag={}, key={}, messageId={}",
                    topic, tag, key, response.messageId());
        } catch (Exception e) {
            LOGGER.error("Error sending message to SNS, topic={}, tag={}, key={}", topic, tag, key, e);
        }
    }
}
