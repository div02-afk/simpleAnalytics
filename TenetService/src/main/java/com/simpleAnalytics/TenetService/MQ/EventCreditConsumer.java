package com.simpleAnalytics.TenetService.MQ;

import com.simpleAnalytics.protobuf.EventProto;

public interface EventCreditConsumer {
    public void consume(EventProto.EventCreditConsumptionInfo eventCreditConsumptionInfo);
}
