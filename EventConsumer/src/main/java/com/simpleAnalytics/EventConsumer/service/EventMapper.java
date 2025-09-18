package com.simpleAnalytics.EventConsumer.service;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.simpleAnalytics.EventConsumer.entity.Event;
import com.simpleAnalytics.EventConsumer.entity.UserEvent;
import com.simpleAnalytics.protobuf.EventProto;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class EventMapper {

    private static com.google.protobuf.Timestamp toProtoTimestamp(Timestamp timestamp) {

        return com.google.protobuf.Timestamp.newBuilder().setSeconds(timestamp.getTime() / 1000).setNanos(timestamp.getNanos()).build();
    }

    private static Timestamp toJavaTimestamp(com.google.protobuf.Timestamp protoTimestamp) {
        if (protoTimestamp == null) return null;

        long millis = protoTimestamp.getSeconds() * 1000L + protoTimestamp.getNanos() / 1_000_000L;
        return new Timestamp(millis);
    }

    public static EventProto.Event toProto(Event event) {
        EventProto.Event.Builder builder = EventProto.Event.newBuilder().setId(event.getId().toString()) // UUID → string
                .setReceivedAt(toProtoTimestamp(event.getReceivedAt())) // Timestamp → epoch millis
                .setSchemaVersion(EventProto.SchemaVersion.valueOf(event.getSchemaVersion().name()));

        // map Context if not null
        if (event.getContext() != null) {
            builder.setContext(EventProto.Context.newBuilder().setIp(nullSafe(event.getContext().getIp())).setUserAgent(nullSafe(event.getContext().getUserAgent())).setOs(nullSafe(event.getContext().getOs())).setBrowser(nullSafe(event.getContext().getBrowser())).setDevice(nullSafe(event.getContext().getDevice())).setLocale(nullSafe(event.getContext().getLocale())).setTimezone(nullSafe(event.getContext().getTimezone())));
        }

        // map UserEvent if not null
        if (event.getUserEvent() != null) {
            EventProto.UserEvent.Builder userEventBuilder = EventProto.UserEvent.newBuilder().setAppId(event.getUserEvent().getAppId().toString()).setAnonymousId(event.getUserEvent().getAnonymousId().toString()).setSessionId(event.getUserEvent().getSessionId().toString()).setUserId(event.getUserEvent().getUserId().toString()).setTimestamp(toProtoTimestamp(event.getUserEvent().getTimestamp())).setEventType(event.getUserEvent().getEventType()).setSource(event.getUserEvent().getSource());

            // handle metadata map
            if (event.getUserEvent().getMetadata() != null) {
                userEventBuilder.setMetadata(Struct.newBuilder().putAllFields(event.getUserEvent().getMetadata().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Value.newBuilder().setStringValue(e.getValue().toString()).build()))).build());
            }

            builder.setUserEvent(userEventBuilder);
        }

        return builder.build();
    }

    public static Event toJava(EventProto.Event protoEvent) {
        Event javaEvent = new Event();

        javaEvent.setId(UUID.fromString(protoEvent.getId()));
        javaEvent.setReceivedAt(toJavaTimestamp(protoEvent.getReceivedAt()));
        javaEvent.setSchemaVersion(protoEvent.getSchemaVersion());
        javaEvent.setContext(protoEvent.getContext());

        UserEvent userEvent = UserEvent.builder()
                .userId(UUID.fromString(protoEvent.getUserEvent().getUserId()))
                .appId(UUID.fromString(protoEvent.getUserEvent().getAppId()))
                .sessionId(UUID.fromString(protoEvent.getUserEvent().getSessionId()))
                .anonymousId(UUID.fromString(protoEvent.getUserEvent().getAnonymousId()))
                .eventType(protoEvent.getUserEvent().getEventType())
                .source(protoEvent.getUserEvent().getSource())
                .metadata(structToMap(protoEvent.getUserEvent().getMetadata()))
                .timestamp(toJavaTimestamp(protoEvent.getUserEvent().getTimestamp()))
                .build();
        javaEvent.setUserEvent(userEvent);

        // Add other fields as needed
        return javaEvent;
    }

    private static Map<String, Object> structToMap(Struct struct) {
        Map<String, Object> map = new HashMap<>();
        if (struct == null) return map;

        struct.getFieldsMap().forEach((key, value) -> map.put(key, valueToObject(value)));
        return map;
    }

    private static Object valueToObject(Value value) {
        return switch (value.getKindCase()) {
            case BOOL_VALUE -> value.getBoolValue();
            case NUMBER_VALUE -> value.getNumberValue();
            case STRING_VALUE -> value.getStringValue();
            case STRUCT_VALUE -> structToMap(value.getStructValue());
            case LIST_VALUE ->
                    value.getListValue().getValuesList().stream().map(EventMapper::valueToObject).collect(Collectors.toList());
            default -> null;
        };
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}

