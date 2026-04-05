package com.findu.common.event.publisher;

import com.findu.common.event.EventPublisher;

/**
 * 领域事件发布器通用实现。
 * 将领域事件发布到内部事件总线，由事件总线进行分发。
 * 各服务可继承或组合此类实现具体的领域事件发布。
 */
public class DomainEventPublisherImpl {

    private final EventPublisher eventPublisher;

    public DomainEventPublisherImpl(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布事件到内部事件总线。
     *
     * @param event 事件对象
     * @param <T>   事件类型
     */
    public <T> void publish(T event) {
        eventPublisher.publish(event);
    }
}
