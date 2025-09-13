package com.simpleAnalytics.Gateway.entity;


import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class Event extends UserEvent {
    UUID Id;
    Timestamp receivedAt;
    Context context;
    SchemaVersion schemaVersion;

    public void UserEvent(UserEvent userEvent) {
        this.setEvent(userEvent.getEvent());
        this.setUserId(userEvent.getUserId());
        this.setAnonymousId(userEvent.getAnonymousId());
        this.setAppId(userEvent.getAppId());
        this.setTimestamp(userEvent.getTimestamp());
        this.setMetadata(userEvent.getMetadata());
        this.setSessionId(userEvent.getSessionId());

    }
}
