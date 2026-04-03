package com.findu.common.event;

/**
 * 领域事件发布器接口。
 * 领域层通过此接口发布事件到内部事件总线。
 */
public interface EventPublisher {

    /**
     * 发布领域事件到内部事件总线。
     *
     * @param event 领域事件对象
     * @param <T>   事件类型
     */
    <T> void publish(T event);
}

