package com.simpleAnalytics.Gateway.cache;

import java.util.UUID;

public interface APIKeyValidityCheck {

    public void isAPIKeyValid(UUID apiKey);
}
