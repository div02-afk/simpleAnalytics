package com.simpleAnalytics.EventConsumer.entity;


import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
public class Event {
    private final UUID Id;
    private final Timestamp receivedAt;
    private Context context;
    private final SchemaVersion schemaVersion;
    private final UserEvent userEvent;

}
