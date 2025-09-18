package com.simpleAnalytics.Gateway.MQ;

import com.simpleAnalytics.Gateway.entity.Event;

import java.util.concurrent.ExecutionException;

public interface EventProducer {
    public void sendEvent(String topic, Event event)  throws ExecutionException, InterruptedException;
}
