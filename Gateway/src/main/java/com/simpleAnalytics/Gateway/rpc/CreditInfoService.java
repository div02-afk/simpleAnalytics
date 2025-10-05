package com.simpleAnalytics.Gateway.rpc;

import com.simpleAnalytics.Gateway.entity.CreditInfo;

import java.util.UUID;

public interface CreditInfoService {
    public CreditInfo getCreditInfo(UUID applicationId);
}
