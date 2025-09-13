package com.simpleAnalytics.EventConsumer.repository;


import com.simpleAnalytics.EventConsumer.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;



    public void saveEvent(Event event) {
        String sql = "INSERT INTO events (id, receivedAt, context, schemaVersion) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getId(),
                event.getReceivedAt(),
                event.getContext(),
                event.getSchemaVersion().name());
    }
}
