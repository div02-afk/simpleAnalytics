package com.simpleAnalytics.Gateway.entity;


import lombok.Builder;

@Builder
// used as redis
public record CreditInfo(long creditLimit, long creditsUsed) {
}
