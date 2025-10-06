package com.simpleAnalytics.EventConsumer.repository;

import java.util.List;

import com.simpleAnalytics.EventConsumer.MQ.DLQEventProducer;
import com.simpleAnalytics.EventConsumer.entity.DLQEvent;
import com.simpleAnalytics.EventConsumer.entity.EventBuffer;
import com.simpleAnalytics.EventConsumer.entity.UserEvent;
import com.simpleAnalytics.protobuf.EventProto;
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


    @Scheduled(fixedDelay = 10000)
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
            EventProto.Context ctx = event.getContext();
            UserEvent ue = event.getUserEvent();
            try {
                metadataJson = mapper.writeValueAsString(ue.getMetadata());
            } catch (JsonProcessingException e) {
                log.warn("Error parsing metadata as JsonString: {}", e.getMessage());
                metadataJson = "";
            }

            ps.setObject(1, event.getId());
            ps.setTimestamp(2, event.getReceivedAt());
            if (ctx == null) {
                ps.setString(3, null);
                ps.setString(4, null);
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setString(7, null);
                ps.setString(8, null);
                ps.setString(9, null);
            } else {
                ps.setString(3, ctx.getIp());
                ps.setString(4, ctx.getUserAgent());
                ps.setString(5, ctx.getOs());
                ps.setString(6, ctx.getBrowser());
                ps.setString(7, ctx.getDevice());
                ps.setString(8, ctx.getLocale());
                ps.setString(9, ctx.getTimezone());
            }
            ps.setString(10, event.getSchemaVersion().name());
            ps.setObject(11, ue.getSessionId());
            ps.setObject(12, ue.getUserId());
            ps.setObject(13, ue.getAnonymousId());
            ps.setObject(14, ue.getAppId());
            ps.setTimestamp(15, ue.getTimestamp());
            ps.setString(16, ue.getEventType());
            ps.setString(17, metadataJson);
            ps.setString(18, ue.getSource());
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
