package com.simpleAnalytics.Gateway.service;


import com.simpleAnalytics.Gateway.MQ.CreditEventProducer;
import com.simpleAnalytics.Gateway.MQ.EventProducer;
import com.simpleAnalytics.Gateway.cache.APIKeyValidityCheck;
import com.simpleAnalytics.Gateway.cache.CreditSyncService;
import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.SchemaVersion;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;
import com.simpleAnalytics.Gateway.exception.InvalidUserEvent;
import com.simpleAnalytics.protobuf.EventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

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
    private final CreditSyncService creditSyncService;

    @Retryable(retryFor = RuntimeException.class)
    public void processEvent(UserEvent newUserEvent, UUID apiKey, Context context) throws InvalidUserEvent, InsufficientCreditsException, InvalidAPIKeyException,RuntimeException {


        //throws invalidapikey exception
        apiKeyValidityCheck.checkAPIKeyValidity(apiKey, newUserEvent.getAppId());

        //throws invaliduserevent exception
        validateUserEvent(newUserEvent);

        UUID eventId = UUID.randomUUID();
        EventProto.Event event = EventMapper.toProtoEvent(eventId, CURRENT_SCHEMA_VERSION, newUserEvent, context);
        log.info("Processing event {}", event.getId());

        //send to kafka
        try {
            eventProducer.sendEvent("event", event);
            creditSyncService.incrementCredit(newUserEvent.getAppId());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void validateUserEvent(UserEvent newUserEvent) throws InvalidUserEvent{
        if (newUserEvent.getAnonymousId() == null) {
            throw new InvalidUserEvent("Anonymous Id required");
        } else if (newUserEvent.getEventType() == null) {
            throw new InvalidUserEvent("Event type required");
        } else if (newUserEvent.getAppId() == null) {
            throw new InvalidUserEvent("Application Id required");
        } else if (newUserEvent.getSource() == null) {
            throw new InvalidUserEvent("Source required");
        } else if (newUserEvent.getTimestamp() == null) {
            throw new InvalidUserEvent("Timestamp required");
        }
    }
}
