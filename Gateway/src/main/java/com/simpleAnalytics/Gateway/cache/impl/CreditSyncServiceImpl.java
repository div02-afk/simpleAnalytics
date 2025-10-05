package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.cache.CreditSyncService;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class CreditSyncServiceImpl implements CreditSyncService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final CreditService creditService;

    @Override
    public void checkAndIncrementCreditUtilization(UUID appId) throws InsufficientCreditsException {
        //TODO: make these parallel/ store in a single key
        long creditLimit = creditService.getCreditLimit(appId);
        long creditUtil = creditService.getCreditUtilization(appId);

        if (creditLimit > creditUtil) {
            incrementCredit(appId);
        } else {
            throw new InsufficientCreditsException(appId);
        }
    }

    @Async
    protected void incrementCredit(UUID appId) {
        redisTemplate.opsForValue().increment("app:deltaCreditUtilization:" + appId.toString());
        redisTemplate.opsForValue().increment("app:creditUtilization:" + appId.toString());
    }


}
