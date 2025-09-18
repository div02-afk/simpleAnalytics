package com.simpleAnalytics.TenetService.repository;

import com.simpleAnalytics.TenetService.entity.CreditInfo;

import java.util.UUID;

public interface ApplicationRepository {

    public void appendCredits(UUID applicationId, int creditAmount);
    public CreditInfo getCreditInfo(UUID applicationId);
}
