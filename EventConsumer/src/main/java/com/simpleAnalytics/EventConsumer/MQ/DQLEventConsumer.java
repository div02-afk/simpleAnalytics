package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.entity.Event;

public interface DQLEventConsumer {
    public void consume(DLQEvent event);
}
