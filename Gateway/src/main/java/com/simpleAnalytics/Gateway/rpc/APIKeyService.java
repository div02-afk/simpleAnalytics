package com.simpleAnalytics.Gateway.rpc;

import java.util.Optional;
import java.util.UUID;

public interface APIKeyService {
    public String getApplicationIdForAPIKey(UUID apiKey);

}
