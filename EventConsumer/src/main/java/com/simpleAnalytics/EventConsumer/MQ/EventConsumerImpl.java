package com.simpleAnalytics.EventConsumer.MQ;


import com.simpleAnalytics.EventConsumer.entity.Event;
import com.simpleAnalytics.EventConsumer.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumerImpl implements EventConsumer {

    EventRepository eventRepository;
    EventConsumerImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @KafkaListener(topics = "event", groupId = "analytics-consumer")
    public void consume(Event event) {
        try {
            log.info("Saving Event: {}", event.toString());
            eventRepository.saveEvent(event);
            log.info("Saved Event: {}", event.getId());
            //save to db
        } catch (Exception e) {
            log.error("Error processing event", e);
            //push to dlq
        }
    }
}
