package com.simpleAnalytics.TenetService.repository.impl;

import com.simpleAnalytics.TenetService.entity.CreditInfo;
import com.simpleAnalytics.TenetService.repository.ApplicationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

    @Override
    public void appendCredits(UUID applicationId, int creditAmount) {

    }

    @Override
    public CreditInfo getCreditInfo(UUID applicationId) {
        return null;
    }

}
