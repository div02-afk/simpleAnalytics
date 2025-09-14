package com.simpleAnalytics.Gateway.MQ;


import com.simpleAnalytics.Gateway.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventProducerImpl implements  EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventProducerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, Event event) {
        kafkaTemplate.send(topic, event);
        log.info("Event pushed to Kafka topic={} -> {}", topic, event);
    }
}
