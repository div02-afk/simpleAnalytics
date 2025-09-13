package com.simpleAnalytics.Gateway.service;


import com.simpleAnalytics.Gateway.MQ.EventProducer;
import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.Gateway.entity.SchemaVersion;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventPipelineServiceImpl implements EventPipelineService {

    EventProducer eventProducer;

    @Autowired
    public EventPipelineServiceImpl(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void processEvent(UserEvent newUserEvent, Context context) {
        Event event = Event.builder()
                .Id(UUID.randomUUID())
                .receivedAt(Timestamp.valueOf(LocalDateTime.now()))
                .context(context)
                .schemaVersion(SchemaVersion.V1_0_0)
                .build();
        event.UserEvent(newUserEvent);

        //send to kafka
        eventProducer.sendEvent("event", event);
    }
}
