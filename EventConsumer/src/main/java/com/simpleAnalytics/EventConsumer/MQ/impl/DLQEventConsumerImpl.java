package com.simpleAnalytics.EventConsumer.MQ.impl;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.MQ.DQLEventConsumer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class DLQEventConsumerImpl implements DQLEventConsumer {
    EventRepository eventRepository;
    DLQEventProducer dlqEventProducer;

    @KafkaListener(topics = "event_dlq", groupId = "dlq_consumer")
    public void consume(DLQEvent dlqEvent) {
        log.info("Processing DLQ Event: {}", dlqEvent.getEvent().getId());
        log.info("Error for DLQ Event: {}", dlqEvent.getError().getMessage());

        try {
            log.info("Saving DLQ Event: {}", dlqEvent.getEvent().getId());
            eventRepository.saveEvent(dlqEvent.getEvent());
            log.info("Saved DLQ Event: {}", dlqEvent.getEvent().getId());
        } catch (Exception e) {
            log.error("Error while saving DLQ Event: {}", dlqEvent.getError().getMessage());
            dlqEventProducer.sendEvent(dlqEvent);
        }

    }
}
