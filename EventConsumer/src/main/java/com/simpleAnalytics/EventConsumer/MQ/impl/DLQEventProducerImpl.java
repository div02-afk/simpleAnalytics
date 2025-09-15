package com.simpleAnalytics.EventConsumer.MQ.impl;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DLQEventProducerImpl implements DLQEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DLQEventProducerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(DLQEvent dlqEvent) {
        log.info("Sending DLQ Event: {}", dlqEvent);
        kafkaTemplate.send("event_dlq", dlqEvent);
    }
}
