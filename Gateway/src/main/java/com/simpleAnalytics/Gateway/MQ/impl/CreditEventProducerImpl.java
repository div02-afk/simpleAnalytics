package com.simpleAnalytics.Gateway.MQ.impl;

import com.simpleAnalytics.Gateway.MQ.CreditEventProducer;
import com.simpleAnalytics.Gateway.entity.EventCreditConsumptionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.simpleAnalytics.protobuf.EventProto;

@Service
@RequiredArgsConstructor
public class CreditEventProducerImpl implements CreditEventProducer {

    private final KafkaTemplate<String, EventProto.EventCreditConsumptionInfo> kafkaTemplate;

    @Override
    public void sendCreditUtilizationEvent(String topic, EventCreditConsumptionInfo eventCreditConsumptionInfo) {
        EventProto.EventCreditConsumptionInfo proto = EventProto.EventCreditConsumptionInfo.newBuilder()
                .setApplicationId(eventCreditConsumptionInfo
                        .getApplicationId()
                        .toString())
                .setCreditAmount(eventCreditConsumptionInfo
                        .getCreditAmount())
                .build();
        kafkaTemplate.send(topic, proto);
    }
}
