package com.simpleAnalytics.Gateway.cache;

import java.util.UUID;

public interface CreditService {
    public long getCreditLimit(UUID appId);
    public long getDetlaCreditUtilization(UUID appId);
    public long getCreditUtilization(UUID appId);
    public void cacheCreditLimit(UUID appId, long creditLimit);
    public void cacheCreditUtilization(UUID appId,long creditUtilization);
}
