package com.simpleAnalytics.EventConsumer.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Context {
    String ip;
    String userAgent;
    String os;
    String browser;
    String device;
    String locale;
    String timezone;
}
