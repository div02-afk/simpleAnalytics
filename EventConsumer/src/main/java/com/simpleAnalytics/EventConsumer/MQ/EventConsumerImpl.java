package com.simpleAnalytics.EventConsumer.MQ;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleAnalytics.EventConsumer.entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumerImpl implements EventConsumer {



    @KafkaListener(topics = "event", groupId = "analytics-consumer")
    public void consume(Event event) {
        log.info("Consumer Record: {}", event.getId());
        try {

            //save to db
        }
        catch (Exception e) {
            log.error("Error processing event", e);
            //push to dlq
        }
    }
}
