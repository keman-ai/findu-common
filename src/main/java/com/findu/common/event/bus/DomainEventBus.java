package com.findu.common.event.bus;

import com.findu.common.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 领域事件总线实现。
 * 基于Spring ApplicationEventPublisher实现内部事件分发机制。
 * 实现领域层的EventPublisher接口，基础设施层不关心具体的业务事件类型。
 */
public class DomainEventBus implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DomainEventBus(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <T> void publish(T event) {
        eventPublisher.publishEvent(event);
    }
}

