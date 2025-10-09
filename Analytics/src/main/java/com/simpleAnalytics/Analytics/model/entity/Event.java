package com.simpleAnalytics.Analytics.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event entity representing the event table in ClickHouse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private UUID id;
    private UUID appId;
    private UUID anonymousId;
    private UUID userId;
    private UUID sessionId;
    private String eventType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime receivedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private String source;
    private String metadata;

    // Context fields
    private String contextIp;
    private String contextOs;
    private String contextUa;
    private String contextDevice;
    private String contextBrowser;
    private String contextLocale;
    private String contextTimezone;

    private String schemaVersion;
}
