package com.simpleAnalytics.EventConsumer.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.simpleAnalytics.EventConsumer.entity.Context;
import com.simpleAnalytics.EventConsumer.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new JsonMapper();

    public void saveEvent(Event event) {
        if (event.getContext() == null) {
            event.setContext(new Context());
        }

        String metadataJson;
        try {
            metadataJson = mapper.writeValueAsString(event.getUserEvent().getMetadata());
        } catch (JsonProcessingException e) {
            log.warn("Error parsing metadata as JsonString: {}", e.getMessage());
            metadataJson = "";
        }
        String sql = """
                INSERT INTO event (
                                   id, receivedAt,
                                   context_ip,context_ua,context_os,context_browser,context_device,context_locale,context_timezone, 
                                   schemaVersion,
                                   sessionId,userId,anonymousId,appId,
                                   timestamp,
                                   eventType,
                                   metadata,
                                   source) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,? ,? ,? , ?, ?, ?,? ,?)
                """;
        jdbcTemplate.update(sql,
                event.getId(),
                event.getReceivedAt(),
                event.getContext().getIp(),
                event.getContext().getUserAgent(),
                event.getContext().getOs(),
                event.getContext().getBrowser(),
                event.getContext().getDevice(),
                event.getContext().getLocale(),
                event.getContext().getTimezone(),
                event.getSchemaVersion().name(),
                event.getUserEvent().getSessionId(),
                event.getUserEvent().getUserId(),
                event.getUserEvent().getAnonymousId(),
                event.getUserEvent().getAppId(),
                event.getUserEvent().getTimestamp(),
                event.getUserEvent().getEventType(),
                metadataJson,
                event.getUserEvent().getSource()
        );
    }
}
