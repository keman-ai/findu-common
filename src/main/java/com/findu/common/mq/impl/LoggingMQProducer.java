package com.findu.common.mq.impl;

import com.alibaba.fastjson.JSON;
import com.findu.common.mq.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志MQ生产者实现（开发/测试环境）。
 * 将消息输出到日志，不实际发送到MQ。
 */
public class LoggingMQProducer implements MQProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMQProducer.class);

    @Override
    public void sendMessage(String topic, String tag, String key, Object payload) {
        try {
            String messageJson = JSON.toJSONString(payload);
            LOGGER.info("MQ Message [Topic: {}, Tag: {}, Key: {}]: {}", topic, tag, key, messageJson);
        } catch (Exception e) {
            LOGGER.error("Error logging MQ message for topic: {}, tag: {}, key: {}", topic, tag, key, e);
        }
    }
}

