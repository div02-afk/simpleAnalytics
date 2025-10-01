package com.simpleAnalytics.TenetService.cache;

import com.simpleAnalytics.TenetService.entity.Application;

import java.util.UUID;

public interface CreditService {
    public void setAppLimit(UUID applicationId,long limit);

    public long getAppCreditLimit(UUID applicationId);

    public long getAppCreditUtilization(UUID applicationId);

}
