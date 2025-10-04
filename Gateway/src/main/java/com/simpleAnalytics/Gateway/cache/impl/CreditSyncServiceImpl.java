package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.cache.CreditSyncService;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class CreditSyncServiceImpl implements CreditSyncService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final CreditService creditService;

    public void incrementCredit(UUID appId) throws InsufficientCreditsException {
        long creditLimit = creditService.getCreditLimit(appId);
        long creditUtil = creditService.getCreditUtilization(appId);

        if (creditLimit > creditUtil) {
            redisTemplate.opsForValue().increment("app:deltaCreditUtilization:" + appId.toString());
            redisTemplate.opsForValue().increment("app:creditUtilization:" + appId.toString());
        } else {
            throw new InsufficientCreditsException(appId);
        }

    }


}
