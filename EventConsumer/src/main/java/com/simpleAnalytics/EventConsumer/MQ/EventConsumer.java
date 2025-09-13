package com.simpleAnalytics.EventConsumer.MQ;

import com.simpleAnalytics.EventConsumer.entity.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface EventConsumer {
    public void consume(Event event);
}
