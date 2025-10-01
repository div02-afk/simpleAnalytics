package com.simpleAnalytics.TenetService.entity;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
// used as redis
public class CreditInfo {
    final long creditLimit;
}
