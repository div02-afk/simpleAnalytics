package com.simpleAnalytics.EventConsumer.MQ.impl;


import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.simpleAnalytics.EventConsumer.repository.EventRepository;
import com.simpleAnalytics.EventConsumer.service.EventMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.MQ.EventConsumer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.entity.Event;
import com.simpleAnalytics.EventConsumer.repository.EventRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.simpleAnalytics.protobuf.EventProto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumerImpl implements EventConsumer {
    private final EventRepository eventRepository;
    private final DLQEventProducer dlqEventProducer;



    @Override
    @KafkaListener(topics = "event", groupId = "analytics-consumer")
    public void consume(EventProto.Event protoEvent) {
            Event event = EventMapper.toJava(protoEvent);
        try {
//            log.info("Saving Event: {}", event.getId());

            eventRepository.save(event);
            log.info("Saved Event: {}", event.getId());
            //save to db
        } catch (Exception e) {
            log.error("Error processing event", e);
            DLQEvent dlqEvent = DLQEvent.builder().errorMessage(e.getMessage()).event(event).build();
//            dlqEventProducer.sendEvent(dlqEvent);
        }
    }
}
