package com.simpleAnalytics.TenetService.MQ;

import com.simpleAnalytics.TenetService.entity.EventCreditConsumptionInfo;

import java.util.UUID;

public interface EventCreditConsumer {
    public void consume(EventCreditConsumptionInfo eventCreditConsumptionInfo);
}
