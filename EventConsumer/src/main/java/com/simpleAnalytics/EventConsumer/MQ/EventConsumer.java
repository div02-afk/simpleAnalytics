package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.Event;

public interface EventConsumer {
    public void consume(Event event);
}
