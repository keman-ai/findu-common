package com.findu.common.mq.impl;

import java.io.Serializable;

/**
 * 事件消息包装类。
 */
public class EventMessage implements Serializable {

    /**
     * 事件类型。
     */
    private String eventType;

    /**
     * 事件数据。
     */
    private Object payload;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

