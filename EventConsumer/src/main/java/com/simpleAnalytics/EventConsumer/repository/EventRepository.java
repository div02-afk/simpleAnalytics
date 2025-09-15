package com.simpleAnalytics.EventConsumer.repository;

import com.simpleAnalytics.EventConsumer.entity.Event;

import java.util.List;

public interface EventRepository {
    public void save(Event event);
    public void saveAll(List<Event> event);
}
