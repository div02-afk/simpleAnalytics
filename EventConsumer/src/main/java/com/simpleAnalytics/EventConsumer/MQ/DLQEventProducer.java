package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.DLQEvent;

public interface DLQEventProducer {

    public void sendEvent(DLQEvent dlqEvent) ;
}
