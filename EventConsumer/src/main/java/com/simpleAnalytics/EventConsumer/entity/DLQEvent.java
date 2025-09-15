package com.simpleAnalytics.EventConsumer.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DLQEvent {
    private final Event event;
    private final Error error;
}
