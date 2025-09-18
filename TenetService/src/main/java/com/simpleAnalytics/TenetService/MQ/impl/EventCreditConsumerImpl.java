package com.simpleAnalytics.TenetService.MQ.impl;

import com.simpleAnalytics.TenetService.MQ.EventCreditConsumer;
import com.simpleAnalytics.TenetService.entity.EventCreditConsumptionInfo;
import com.simpleAnalytics.TenetService.exception.InsufficientCreditsException;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventCreditConsumerImpl implements EventCreditConsumer {
    private final KafkaTemplate<String, EventCreditConsumptionInfo> kafkaTemplate;
    private final ApplicationService applicationService;


    @KafkaListener(topics = "creditUtilization", groupId = "credit-consumer")
    public void consume(EventCreditConsumptionInfo eventCreditConsumptionInfo) {
        try {
            log.info("Consuming {}", eventCreditConsumptionInfo);
            applicationService.useCredit(eventCreditConsumptionInfo.getApplicationId(), eventCreditConsumptionInfo.getCreditAmount());
        } catch (InsufficientCreditsException e) {

            //TODO: let gateway know about expired limits

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
