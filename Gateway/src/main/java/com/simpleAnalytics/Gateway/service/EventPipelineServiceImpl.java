package com.simpleAnalytics.Gateway.service;


import com.simpleAnalytics.Gateway.MQ.CreditEventProducer;
import com.simpleAnalytics.Gateway.MQ.EventProducer;
import com.simpleAnalytics.Gateway.MQ.impl.EventProducerImpl;
import com.simpleAnalytics.Gateway.cache.APIKeyValidityCheck;
import com.simpleAnalytics.Gateway.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPipelineServiceImpl implements EventPipelineService {

    private final EventProducer eventProducer;
    private final CreditEventProducer creditEventProducer;
    private final SchemaVersion CURRENT_SCHEMA_VERSION = SchemaVersion.V1_0_0;
    private final APIKeyValidityCheck apiKeyValidityCheck;

    @Retryable(retryFor = RuntimeException.class)
    public void processEvent(UserEvent newUserEvent,UUID apiKey, Context context) throws RuntimeException {
        validateUserEvent(newUserEvent);

        Event event = Event.builder()
                .Id(UUID.randomUUID())
                .receivedAt(Timestamp.valueOf(LocalDateTime.now()))
                .context(context)
                .schemaVersion(CURRENT_SCHEMA_VERSION)
                .userEvent(newUserEvent)
                .build();
        log.info("Processing event {}", event.getId());
        EventCreditConsumptionInfo eventCreditConsumptionInfo = EventCreditConsumptionInfo
                .builder()
                .applicationId(event.getId())
                .creditAmount(1)
                .build();

        apiKeyValidityCheck.isAPIKeyValid(apiKey);

        //send to kafka
        try{

        eventProducer.sendEvent("event", event);
        creditEventProducer.sendCreditUtilizationEvent("creditUtilization", eventCreditConsumptionInfo);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validateUserEvent(UserEvent newUserEvent) {
        if (newUserEvent.getAnonymousId() == null) {
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
