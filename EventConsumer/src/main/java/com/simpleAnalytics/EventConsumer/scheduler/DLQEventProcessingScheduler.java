package com.simpleAnalytics.EventConsumer.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
public class DLQEventProcessingScheduler {

    private final KafkaListenerEndpointRegistry registry;

    @Scheduled(fixedDelay = 60000) // every 60s
    public void triggerDlqConsumer() throws InterruptedException {
        var container = registry.getListenerContainer("event_dlq");
        assert container != null;
        container.start();

        // let it run for 5sec
        Thread.sleep(5000);

        container.stop();
    }

}
