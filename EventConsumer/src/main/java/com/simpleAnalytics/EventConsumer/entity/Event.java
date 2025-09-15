package com.simpleAnalytics.EventConsumer.entity;


import lombok.*;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private UUID Id;
    private Timestamp receivedAt;
    @Nullable
    private Context context;
    private SchemaVersion schemaVersion;
    private UserEvent userEvent;

}
