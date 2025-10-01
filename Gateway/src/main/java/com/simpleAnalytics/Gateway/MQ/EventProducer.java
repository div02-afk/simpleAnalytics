package com.simpleAnalytics.Gateway.MQ;

import com.simpleAnalytics.protobuf.EventProto;

import java.util.concurrent.ExecutionException;

public interface EventProducer {
    public void sendEvent(String topic, EventProto.Event event)  throws ExecutionException, InterruptedException;
}
