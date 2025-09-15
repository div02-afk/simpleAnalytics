package com.simpleAnalytics.EventConsumer.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class Context {
    private final String ip;
    private final String userAgent;
    private final String os;
    private final String browser;
    private final String device;
    private final String locale;
    private final String timezone;
}
