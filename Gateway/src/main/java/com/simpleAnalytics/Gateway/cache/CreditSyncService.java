package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface CreditSyncService {
    public void checkAndIncrementCreditUtilization(UUID appId) throws InsufficientCreditsException, ExecutionException, InterruptedException;
}
