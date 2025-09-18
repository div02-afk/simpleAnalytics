package com.simpleAnalytics.Gateway.service;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.protobuf.EventProto;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class EventMapper {

    private static com.google.protobuf.Timestamp toProtoTimestamp(Timestamp timestamp) {

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(timestamp.getTime() / 1000)
                .setNanos(timestamp.getNanos())
                .build();
    }

    public static EventProto.Event toProto(Event event) {
        EventProto.Event.Builder builder = EventProto.Event.newBuilder()
                .setId(event.getId().toString()) // UUID → string
                .setReceivedAt(toProtoTimestamp(event.getReceivedAt())) // Timestamp → epoch millis
                .setSchemaVersion(EventProto.SchemaVersion.valueOf(event.getSchemaVersion().name()));

        // map Context if not null
        if (event.getContext() != null) {
            builder.setContext(
                    EventProto.Context.newBuilder()
                            .setIp(nullSafe(event.getContext().getIp()))
                            .setUserAgent(nullSafe(event.getContext().getUserAgent()))
                            .setOs(nullSafe(event.getContext().getOs()))
                            .setBrowser(nullSafe(event.getContext().getBrowser()))
                            .setDevice(nullSafe(event.getContext().getDevice()))
                            .setLocale(nullSafe(event.getContext().getLocale()))
                            .setTimezone(nullSafe(event.getContext().getTimezone()))
            );
        }

        // map UserEvent if not null
        if (event.getUserEvent() != null) {
            EventProto.UserEvent.Builder userEventBuilder = EventProto.UserEvent.newBuilder()
                    .setAppId(event.getUserEvent().getAppId().toString())
                    .setAnonymousId(event.getUserEvent().getAnonymousId().toString())
                    .setSessionId(event.getUserEvent().getSessionId().toString())
                    .setUserId(event.getUserEvent().getUserId().toString())
                    .setTimestamp(toProtoTimestamp(event.getUserEvent().getTimestamp()))
                    .setEventType(event.getUserEvent().getEventType())
                    .setSource(event.getUserEvent().getSource());

            // handle metadata map
            if (event.getUserEvent().getMetadata() != null) {
                userEventBuilder.setMetadata(
                        Struct.newBuilder()
                                .putAllFields(event.getUserEvent().getMetadata().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Value.newBuilder().setStringValue(e.getValue().toString()).build())))
                                .build()
                );
            }

            builder.setUserEvent(userEventBuilder);
        }

        return builder.build();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}

