package com.simpleAnalytics.TenetService.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@Data
@RequiredArgsConstructor
public class EventCreditConsumptionInfo {
    private final UUID applicationId;
    private final int creditAmount;
}
