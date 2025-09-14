package com.simpleAnalytics.Gateway.MQ;

import com.simpleAnalytics.Gateway.entity.Event;

public interface EventProducer {
    public void sendEvent(String topic, Event event);
}
