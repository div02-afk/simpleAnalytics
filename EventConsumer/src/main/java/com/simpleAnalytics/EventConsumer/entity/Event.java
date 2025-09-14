package com.simpleAnalytics.EventConsumer.entity;


import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Event extends UserEvent {
    UUID Id;
    Timestamp receivedAt;
    Context context;
    SchemaVersion schemaVersion;

    public void UserEvent(UserEvent userEvent) {
        this.setEventType(userEvent.getEventType());
        this.setUserId(userEvent.getUserId());
        this.setAnonymousId(userEvent.getAnonymousId());
        this.setAppId(userEvent.getAppId());
        this.setTimestamp(userEvent.getTimestamp());
        this.setMetadata(userEvent.getMetadata());
        this.setSessionId(userEvent.getSessionId());
        this.setSource(userEvent.getSource());
    }
}
