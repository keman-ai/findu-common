package com.findu.common.mq;

/**
 * MQ消息生产者接口。
 * 基础设施层提供MQ发送能力，不关心具体的业务事件类型。
 */
public interface MQProducer {

    /**
     * 发送消息到MQ。
     *
     * @param topic     主题
     * @param tag       标签（事件类型）
     * @param key       消息键
     * @param payload   消息体（事件对象）
     */
    void sendMessage(String topic, String tag, String key, Object payload);
}

