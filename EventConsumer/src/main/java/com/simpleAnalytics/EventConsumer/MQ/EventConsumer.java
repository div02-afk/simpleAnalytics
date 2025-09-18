package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.Event;
import com.simpleAnalytics.protobuf.EventProto;

public interface EventConsumer {
    public void consume(EventProto.Event protoEvent);
}
