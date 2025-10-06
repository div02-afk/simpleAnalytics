package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.entity.CreditInfo;
import com.simpleAnalytics.Gateway.rpc.CreditInfoService;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
//TODO: refactor to consolidate rpc calls and caching to a single method
@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final RedisAsyncCommands<String, Long> redisAsync;
    private final CreditInfoService creditInfoService;

    //TODO add cache invalidation/TTL
    @Override
    public long getCreditLimit(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:creditLimit:" + appId.toString());

        if (creditLimit == null) {
            CreditInfo creditInfo = creditInfoService.getCreditInfo(appId);
            creditLimit = creditInfo.creditLimit();
            cacheCreditLimit(appId, creditInfo.creditLimit());
            cacheCreditUtilization(appId, creditInfo.creditsUsed());
        }

        return creditLimit;
    }

    @Override
    public CreditInfo getCreditInfo(UUID appId) throws ExecutionException, InterruptedException {
        RedisFuture<Long> creditUtilF =  redisAsync.get("app:creditUtilization:" + appId);
        RedisFuture<Long> creditLimitF = redisAsync.get("app:creditLimit:" + appId);

        Long creditUtilization = creditUtilF.get();
        Long creditLimit = creditLimitF.get();
        if(creditLimit == null || creditUtilization == null) {
            CreditInfo creditInfo = creditInfoService.getCreditInfo(appId);
            cacheCreditLimit(appId, creditInfo.creditLimit());
            cacheCreditUtilization(appId, creditInfo.creditsUsed());
            return creditInfo;
        }
        return new CreditInfo(creditUtilization, creditLimit);

    }

    @Override
    public long getDetlaCreditUtilization(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:deltaCreditUtilization:" + appId);
        return creditLimit == null ? 0 : creditLimit;
    }


    @Override
    public long getCreditUtilization(UUID appId) {
        Long creditUtilized = redisTemplate.opsForValue().get("app:creditUtilization:" + appId);

        if (creditUtilized == null) {
            CreditInfo creditInfo = creditInfoService.getCreditInfo(appId);
            creditUtilized = creditInfo.creditsUsed();
            cacheCreditLimit(appId, creditInfo.creditLimit());
            cacheCreditUtilization(appId, creditInfo.creditsUsed());
        }

        return creditUtilized;
    }


    @Override
    public void cacheCreditLimit(UUID appId, long creditLimit) {
        redisAsync.set("app:creditLimit:" + appId, creditLimit);
    }


    @Override
    public void cacheCreditUtilization(UUID appId, long creditUtilization) {
        redisAsync.set("app:creditUtilization:" + appId, creditUtilization);
    }
}
