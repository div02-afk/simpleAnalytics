package com.simpleAnalytics.Gateway.cache;

import java.util.UUID;

public interface CreditService {
    public long getCreditLimit(UUID appId);

    public long getCreditUtilization(UUID appId);
}
