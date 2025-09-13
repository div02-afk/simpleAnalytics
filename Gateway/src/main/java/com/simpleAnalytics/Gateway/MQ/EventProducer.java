package com.simpleAnalytics.Gateway.MQ;


import com.simpleAnalytics.Gateway.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, Event event) {
        kafkaTemplate.send(topic, event);
        log.info("Event pushed to Kafka topic={} -> {}", topic, event);
    }
}
