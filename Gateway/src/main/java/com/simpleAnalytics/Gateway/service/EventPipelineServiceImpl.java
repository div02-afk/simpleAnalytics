package com.simpleAnalytics.Gateway.service;


import com.simpleAnalytics.Gateway.MQ.EventProducerImpl;
import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.Gateway.entity.SchemaVersion;
import com.simpleAnalytics.Gateway.entity.UserEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventPipelineServiceImpl implements EventPipelineService {

    private final EventProducerImpl eventProducerImpl;
    private final SchemaVersion CURRENT_SCHEMA_VERSION = SchemaVersion.V1_0_0;
    

    public void processEvent(UserEvent newUserEvent, Context context) throws RuntimeException {
        validateUserEvent(newUserEvent);

        Event event = Event.builder()
                .Id(UUID.randomUUID())
                .receivedAt(Timestamp.valueOf(LocalDateTime.now()))
                .context(context)
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .userEvent(newUserEvent)
                .build();


        //send to kafka
        eventProducerImpl.sendEvent("event", event);
    }

    private static void validateUserEvent(UserEvent newUserEvent) {
        if(newUserEvent.getAnonymousId() == null){
            throw new RuntimeException("Anonymous Id required");
        } else if (newUserEvent.getEventType() == null) {
            throw new RuntimeException("Event type required");
        } else if (newUserEvent.getAppId() == null) {
            throw new RuntimeException("Application Id required");
        } else if (newUserEvent.getSource() == null) {
            throw new RuntimeException("Source required");
        } else if (newUserEvent.getTimestamp() == null) {
            throw new RuntimeException("Timestamp required");
        }
    }
}
