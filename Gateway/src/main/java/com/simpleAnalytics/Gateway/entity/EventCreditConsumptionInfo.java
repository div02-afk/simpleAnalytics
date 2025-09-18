package com.simpleAnalytics.Gateway.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@Data
@RequiredArgsConstructor
@Builder
public class EventCreditConsumptionInfo {
    private final UUID applicationId;
    private final int creditAmount;
}
