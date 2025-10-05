package com.simpleAnalytics.Gateway.cache;

import java.util.UUID;

public interface APIKeyValidityCheck {
    public void cacheAPIKeyApplicationId(UUID apikey, UUID applicationId);
    public String getCachedAPIKeyApplicationId(UUID apikey);
}


