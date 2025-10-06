package com.simpleAnalytics.Gateway.MQ.impl;


import com.simpleAnalytics.Gateway.MQ.EventProducer;
import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.Gateway.service.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.simpleAnalytics.protobuf.EventProto;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducerImpl implements EventProducer {
    private final KafkaTemplate<String, EventProto.Event> eventKafkaTemplate;
    @Override
    public void sendEvent(String topic, EventProto.Event event) throws ExecutionException, InterruptedException {
        eventKafkaTemplate.send(topic, event).get();
//        log.info("Event pushed to Kafka topic={} -> {}", topic, event.getId());
    }
}
