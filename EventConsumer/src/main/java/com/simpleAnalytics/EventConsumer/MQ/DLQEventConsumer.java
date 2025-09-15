package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.DLQEvent;

import java.util.List;

public interface DLQEventConsumer {
    public void consume(List<DLQEvent> event);
}
