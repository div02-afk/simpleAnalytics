package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;

import java.util.UUID;

public interface APIKeyValidityCheck {

    public void isAPIKeyValid(UUID apiKey) throws InsufficientCreditsException, InvalidAPIKeyException;
}
