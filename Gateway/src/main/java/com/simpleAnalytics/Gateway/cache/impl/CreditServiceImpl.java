package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.entity.CreditInfo;
import com.simpleAnalytics.Gateway.rpc.CreditInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final CreditInfoService creditInfoService;

    //TODO add cache invalidation/TTL
    @Override
    public long getCreditLimit(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:creditLimit:" + appId.toString());

        if (creditLimit == null) {
            CreditInfo creditInfo = creditInfoService.getCreditInfo(appId);
            creditLimit = creditInfo.creditLimit();
            CompletableFuture.runAsync(() -> {
                cacheCreditLimit(appId, creditInfo.creditLimit());
                cacheCreditUtilization(appId, creditInfo.creditsUsed());
            });
        }

        return creditLimit;
    }

    @Override
    public long getDetlaCreditUtilization(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:deltaCreditUtilization:" + appId.toString());
        return creditLimit == null ? 0 : creditLimit;
    }


    @Override
    public long getCreditUtilization(UUID appId) {
        Long creditUtilized = redisTemplate.opsForValue().get("app:creditUtilization:" + appId.toString());

        if (creditUtilized == null) {
            CreditInfo creditInfo = creditInfoService.getCreditInfo(appId);
            creditUtilized = creditInfo.creditsUsed();
            CompletableFuture.runAsync(() -> {
                cacheCreditLimit(appId, creditInfo.creditLimit());
                cacheCreditUtilization(appId, creditInfo.creditsUsed());
            });
        }

        return creditUtilized;
    }


    @Override
    public void cacheCreditLimit(UUID appId, long creditLimit) {
        redisTemplate.opsForValue().set("app:creditLimit:" + appId.toString(), creditLimit);
    }


    @Override
    public void cacheCreditUtilization(UUID appId, long creditUtilization) {
        redisTemplate.opsForValue().set("app:creditUtilization:" + appId.toString(), creditUtilization);
    }
}
