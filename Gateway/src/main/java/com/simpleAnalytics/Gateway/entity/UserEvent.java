package com.simpleAnalytics.Gateway.entity;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Data
public class UserEvent {
    private UUID appId;
    private UUID anonymousId;
    private UUID sessionId;
    private UUID userId;
    private Timestamp timestamp;
    private String event;
    private String source;

    private Map<String, Object> metadata;
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

