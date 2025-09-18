package com.simpleAnalytics.Gateway.MQ;

import com.simpleAnalytics.Gateway.entity.EventCreditConsumptionInfo;

public interface CreditEventProducer {
    public void sendCreditUtilizationEvent(String topic, EventCreditConsumptionInfo eventCreditConsumptionInfo);
}
