package com.simpleAnalytics.EventConsumer.entity;


import com.simpleAnalytics.protobuf.EventProto;
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
    private EventProto.Context context;
    private EventProto.SchemaVersion schemaVersion;
    private UserEvent userEvent;

}
