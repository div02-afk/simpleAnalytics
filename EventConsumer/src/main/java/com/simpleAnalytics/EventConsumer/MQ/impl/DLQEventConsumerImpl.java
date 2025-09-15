package com.simpleAnalytics.EventConsumer.MQ.impl;

import com.simpleAnalytics.EventConsumer.repository.EventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventConsumer;
import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.repository.EventRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DLQEventConsumerImpl implements DLQEventConsumer {
    private final EventRepository eventRepository;
    private final DLQEventProducer dlqEventProducer;


    @Override
    @KafkaListener(topics = "event_dlq", groupId = "dlq_consumer")
    public void consume(List<DLQEvent> dlqEvents) {
        log.info("Processing DLQ Event: {}", dlqEvents.size());
        log.info("Error for DLQ Event: {}", dlqEvents.getFirst().getError().getMessage());

        try {
            log.info("Saving DLQ Events");
            eventRepository.saveAll(dlqEvents.stream().map(DLQEvent::getEvent).toList());
            log.info("Saved DLQ Events: {}", dlqEvents.size());
        } catch (Exception e) {
            log.error("Error while saving DLQ Events: {}", dlqEvents.stream().map(dlqEvent -> dlqEvent.getEvent().getId()).toList());
//            dlqEventProducer.sendEvent(dlqEvent);
        }

    }
}
