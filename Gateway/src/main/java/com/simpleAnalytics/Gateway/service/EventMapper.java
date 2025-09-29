package com.simpleAnalytics.Gateway.service;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.Event;
import com.simpleAnalytics.Gateway.entity.SchemaVersion;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import com.simpleAnalytics.protobuf.EventProto;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
public class EventMapper {

    private static com.google.protobuf.Timestamp toProtoTimestamp(Timestamp ts) {
        long seconds = ts.getTime() / 1000;
        int nanos = ts.getNanos();
        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
    }

    public static EventProto.Context toProtoContext(Context ctx) {
        EventProto.Context.Builder builder = EventProto.Context.newBuilder();
        safeSet(builder::setIp, ctx.getIp());
        safeSet(builder::setUserAgent, ctx.getUserAgent());
        safeSet(builder::setOs, ctx.getOs());
        safeSet(builder::setBrowser, ctx.getBrowser());
        safeSet(builder::setDevice, ctx.getDevice());
        safeSet(builder::setLocale, ctx.getLocale());
        safeSet(builder::setTimezone, ctx.getTimezone());
        return builder.build();
    }

    private static void safeSet(Consumer<String> setter, String value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public static EventProto.UserEvent toProtoUserEvent(UserEvent ue) {
        EventProto.UserEvent.Builder builder = EventProto.UserEvent.newBuilder()
                .setAppId(ue.getAppId().toString())
                .setAnonymousId(ue.getAnonymousId().toString())
                .setSessionId(ue.getSessionId().toString())
                .setUserId(ue.getUserId().toString())
                .setTimestamp(toProtoTimestamp(ue.getTimestamp()))
                .setEventType(ue.getEventType())
                .setSource(ue.getSource());

        if (ue.getMetadata() != null && !ue.getMetadata().isEmpty()) {
            builder.setMetadata(toProtoStruct(ue.getMetadata()));
        }

        return builder.build();
    }

    public static EventProto.Event toProtoEvent(UUID eventId, SchemaVersion defaultSchemaVersion, UserEvent ue, Context context) {
        EventProto.Event.Builder builder = EventProto.Event.newBuilder()
                .setId(eventId.toString())
                .setReceivedAt(com.google.protobuf.Timestamp.getDefaultInstance())
                .setSchemaVersion(EventProto.SchemaVersion.valueOf(defaultSchemaVersion.toString()))
                ;

        if (context != null) {
            builder.setContext(toProtoContext(context));
        }

        if (ue != null) {
            builder.setUserEvent(toProtoUserEvent(ue));
        }

        return builder.build();
    }


    private static Struct toProtoStruct(Map<String, ?> metadata) {
        Struct.Builder structBuilder = Struct.newBuilder();
        metadata.forEach((k, v) -> {
            if (v != null) {
                structBuilder.putFields(k,
                        Value.newBuilder().setStringValue(v.toString()).build()
                );
            }
        });
        return structBuilder.build();
    }


    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}

