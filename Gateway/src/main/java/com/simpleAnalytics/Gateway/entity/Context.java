package com.simpleAnalytics.Gateway.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Context {
    private String ip;
    private String userAgent;
    private String os;
    private String browser;
    private String device;
    private String locale;
    private String timezone;
}
