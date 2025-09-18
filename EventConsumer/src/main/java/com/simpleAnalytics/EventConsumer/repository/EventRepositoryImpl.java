package com.simpleAnalytics.EventConsumer.repository;

import java.util.List;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.entity.EventBuffer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.simpleAnalytics.EventConsumer.entity.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DLQEventProducer dlqEventProducer;
    private final ObjectMapper mapper = new JsonMapper();
    private final EventBuffer events;
    private static final String INSERT_SQL = """
            INSERT INTO event (id, receivedAt,context_ip,context_ua,context_os,
                context_browser,context_device,context_locale,context_timezone, 
                schemaVersion,sessionId,userId,anonymousId,appId,timestamp,
                eventType,metadata,source) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,? ,? ,? , ?, ?, ?,? ,?)
            """;


    @Scheduled(fixedDelay = 5000)
    protected void scheduledBatchSave() {
        flushEvents();
    }

    private void flushEvents() {
        if (events.isEmpty()) {
            return;
        }

        // Process in smaller batches to avoid overwhelming the database
        List<Event> eventBatch;
        while (!(eventBatch = events.drainBatch(500)).isEmpty()) {
            try {
                saveBatch(eventBatch);
                log.debug("Successfully saved batch of {} events", eventBatch.size());
            } catch (Exception e) {
                log.error("Failed to save batch of {} events after retries", eventBatch.size(), e);
                Error error = new Error(e);
                dlqEventProducer.sendEvents(eventBatch.stream().map((Event event) -> DLQEvent.builder().event(event).errorMessage(error.getMessage()).build()).toList());
                log.info("Sending DLQ Events: {}", eventBatch.size());
            }
        }
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000))
    private void saveBatch(List<Event> eventBatch) throws DataAccessException {
        jdbcTemplate.batchUpdate(INSERT_SQL, eventBatch, eventBatch.size(), (ps, event) -> {
            String metadataJson;
            try {
                metadataJson = mapper.writeValueAsString(event.getUserEvent().getMetadata());
            } catch (JsonProcessingException e) {
                log.warn("Error parsing metadata as JsonString: {}", e.getMessage());
                metadataJson = "";
            }
            ps.setObject(1, event.getId());
            ps.setObject(2, event.getReceivedAt());
            ps.setObject(3, event.getContext() == null ? null : event.getContext().getIp());
            ps.setObject(4, event.getContext() == null ? null : event.getContext().getUserAgent());
            ps.setObject(5, event.getContext() == null ? null : event.getContext().getOs());
            ps.setObject(6, event.getContext() == null ? null : event.getContext().getBrowser());
            ps.setObject(7, event.getContext() == null ? null : event.getContext().getDevice());
            ps.setObject(8, event.getContext() == null ? null : event.getContext().getLocale());
            ps.setObject(9, event.getContext() == null ? null : event.getContext().getTimezone());
            ps.setObject(10, event.getSchemaVersion().name());
            ps.setObject(11, event.getUserEvent().getSessionId());
            ps.setObject(12, event.getUserEvent().getUserId());
            ps.setObject(13, event.getUserEvent().getAnonymousId());
            ps.setObject(14, event.getUserEvent().getAppId());
            ps.setObject(15, event.getUserEvent().getTimestamp());
            ps.setObject(16, event.getUserEvent().getEventType());
            ps.setObject(17, metadataJson);
            ps.setObject(18, event.getUserEvent().getSource());
        });
    }


    public void save(Event event) {
        events.add(event);
        // Immediate flush if buffer is getting too large
        shouldFlushBatch();
    }

    public void saveAll(List<Event> eventList) {
        events.addAll(eventList);
        // Immediate flush if buffer is getting too large
        shouldFlushBatch();
    }

    private void shouldFlushBatch() {
        if (events.shouldFlush()) {
            flushEvents();
        }
    }

}
