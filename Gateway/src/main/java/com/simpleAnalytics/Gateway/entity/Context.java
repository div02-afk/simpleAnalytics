package com.simpleAnalytics.Gateway.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    String ip;
    String userAgent;
    String os;
    String browser;
    String device;
    String locale;
    String timezone;
}
