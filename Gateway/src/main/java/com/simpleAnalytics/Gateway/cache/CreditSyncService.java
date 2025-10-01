package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;

import java.util.UUID;

public interface CreditSyncService {

    public void incrementCredit(UUID appId) throws InsufficientCreditsException;
}
