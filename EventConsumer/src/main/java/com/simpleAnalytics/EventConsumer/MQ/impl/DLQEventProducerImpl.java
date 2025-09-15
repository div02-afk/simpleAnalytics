package com.simpleAnalytics.EventConsumer.MQ.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DLQEventProducerImpl implements DLQEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendEvent(DLQEvent dlqEvent) {
        log.info("Sending DLQ Event: {}", dlqEvent);
        kafkaTemplate.send("event_dlq", dlqEvent);
    }

    @Override
    public void sendEvents(List<DLQEvent> dlqEvents) {
        log.info("Sending DLQ Events: {}", dlqEvents);
        kafkaTemplate.send("event_dlq", dlqEvents);
    }
}
