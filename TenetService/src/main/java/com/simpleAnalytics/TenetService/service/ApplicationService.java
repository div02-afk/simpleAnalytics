package com.simpleAnalytics.TenetService.service;

import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.exception.InsufficientCreditsException;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    public UUID createApplication(Application application);

    public Application getApplication(UUID id);

    public List<Application> getAllApplications(UUID tenetId);

    public void updateApplication(UUID id, Application application);

    public void useCredit(UUID applicationId, int creditAmount) throws InsufficientCreditsException;

    public boolean canUseCredit(UUID applicationId, int creditAmount);
}
