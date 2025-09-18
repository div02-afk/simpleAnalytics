package com.simpleAnalytics.Gateway.MQ;


import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.Gateway.service.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.simpleAnalytics.protobuf.EventProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducerImpl implements  EventProducer {
    private final KafkaTemplate<String, EventProto.Event> kafkaTemplate;
    private final EventMapper eventMapper;
    @Override
    public void sendEvent(String topic, Event event) {
        EventProto.Event.Builder builder = EventProto.Event.newBuilder();
        kafkaTemplate.send(topic, EventMapper.toProto(event));
        log.info("Event pushed to Kafka topic={} -> {}", topic, event);
    }
}
