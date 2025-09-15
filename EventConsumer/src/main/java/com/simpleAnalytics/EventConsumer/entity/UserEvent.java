package com.simpleAnalytics.EventConsumer.entity;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Data
public class UserEvent {
    private final UUID appId;
    private final UUID anonymousId;
    private final UUID sessionId;
    private final UUID userId;
    private final Timestamp timestamp;
    private final String eventType;
    private final String source;
    private final Map<String, Object> metadata;
}


//UserEvent schema
//{
//appId
//        userId
//anonymousId
//        sessionId
//event
//        timestamp
//source
//page : {url,ref,title}
//metadata: {
//event specifics,
//user group details
//    }
//            }

