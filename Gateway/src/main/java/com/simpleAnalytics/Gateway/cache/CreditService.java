package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.entity.CreditInfo;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface CreditService {
    public long getCreditLimit(UUID appId);
    public CreditInfo getCreditInfo(UUID appId) throws ExecutionException, InterruptedException;
    public long getDetlaCreditUtilization(UUID appId);
    public long getCreditUtilization(UUID appId);
    public void cacheCreditLimit(UUID appId, long creditLimit);
    public void cacheCreditUtilization(UUID appId,long creditUtilization);
}
