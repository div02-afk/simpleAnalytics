package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import com.simpleAnalytics.Gateway.cache.CreditSyncService;
import com.simpleAnalytics.Gateway.entity.CreditInfo;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RequiredArgsConstructor
@Service
public class CreditSyncServiceImpl implements CreditSyncService {

    private final CreditService creditService;
    private final RedisAsyncCommands<String, Long> redisAsync;

    @Override
    public void checkAndIncrementCreditUtilization(UUID appId) throws InsufficientCreditsException, ExecutionException, InterruptedException {

        CreditInfo creditInfo = creditService.getCreditInfo(appId);

        if (creditInfo.creditLimit() > creditInfo.creditsUsed()) {
            incrementCredit(appId.toString());
        } else {
            throw new InsufficientCreditsException(appId);
        }
    }


    private void incrementCredit(String appId) {
        redisAsync.incr("app:deltaCreditUtilization:" + appId);
        redisAsync.incr("app:creditUtilization:" + appId);
    }


}
