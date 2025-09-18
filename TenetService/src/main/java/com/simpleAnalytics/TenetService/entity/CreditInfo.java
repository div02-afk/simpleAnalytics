package com.simpleAnalytics.TenetService.entity;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@RequiredArgsConstructor
@Builder
public class CreditInfo {
    final int creditLimit;
    final int creditsUsed;
}
