package com.simpleAnalytics.TenetService.cache.impl;

import com.simpleAnalytics.TenetService.cache.CreditSyncService;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class CreditSyncServiceImpl implements CreditSyncService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final ApplicationService applicationService;

//TODO this can be improved with a batch update
    @Scheduled(fixedRate = 5000)
    @Override
    public void syncCreditsToPostgres() {
//        log.info("syncCreditsToPostgres");
        ScanOptions options = ScanOptions.scanOptions()
                .match("app:deltaCreditUtilization:*")
                .count(100) // number of keys per batch
                .build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> {
                Long delta = redisTemplate.opsForValue().get(key);
                if (delta != null && delta > 0) {
                    UUID appId = UUID.fromString(key.split(":")[2]);
                    try {
//                        log.info("Saving credits to database for appId: {}", appId);
                        applicationService.incrementCredits(appId, delta);
                        redisTemplate.opsForValue().decrement(key, delta);
                    } catch (Exception e) {
                        log.error("Error updating credits for app {}", appId, e);
                    }
                }
            });
        }
    }

    //reset monthly
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetCreditLimitsInCache(){
        ScanOptions options = ScanOptions.scanOptions().match("app:creditUtilization:*")
                .count(100)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(redisTemplate::delete);
        }
    }
}
