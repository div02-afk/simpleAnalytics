package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;

import java.util.UUID;

public interface APIKeyValidityCheck {

    public void checkAPIKeyValidity(UUID apiKey, UUID claimedApplicationId) throws InsufficientCreditsException, InvalidAPIKeyException;

    public void cacheAPIKeyApplicationId(UUID apikey, UUID applicationId);
    public String getCachedAPIKeyApplicationId(UUID apikey);
}


