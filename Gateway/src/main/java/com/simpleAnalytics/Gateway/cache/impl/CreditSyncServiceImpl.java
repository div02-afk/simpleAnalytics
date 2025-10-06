package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.cache.CreditSyncService;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Service
public class CreditSyncServiceImpl implements CreditSyncService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final CreditService creditService;

    @Override
    public void checkAndIncrementCreditUtilization(UUID appId) throws InsufficientCreditsException {
        CompletableFuture<Long> creditLimitFuture = CompletableFuture.supplyAsync(() -> creditService.getCreditLimit(appId));
        CompletableFuture<Long> creditUtilFuture = CompletableFuture.supplyAsync(() -> creditService.getCreditUtilization(appId));

        long creditLimit = creditLimitFuture.join();
        long creditUtil = creditUtilFuture.join();

        if (creditLimit > creditUtil) {
            CompletableFuture.runAsync(() -> {
                incrementCredit(appId.toString());
            });
        } else {
            throw new InsufficientCreditsException(appId);
        }
    }


    private void incrementCredit(String appId) {
        redisTemplate.opsForValue().increment("app:deltaCreditUtilization:" + appId);
        redisTemplate.opsForValue().increment("app:creditUtilization:" + appId);
    }


}
