package com.simpleAnalytics.Gateway.entity;


import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Event{
    UUID Id;
    Timestamp receivedAt;
    Context context;
    SchemaVersion schemaVersion;
    UserEvent userEvent;
}
