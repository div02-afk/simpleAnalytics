package com.simpleAnalytics.TenetService.service;

import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.entity.CreditInfo;
import com.simpleAnalytics.TenetService.exception.InsufficientCreditsException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationService {
    public UUID createApplication(UUID tenetId,Application application);

    public Application getApplication(UUID id);

    public List<Application> getAllApplications(UUID tenetId);

    public void updateApplication(UUID id, Application application);

    public void incrementCredits(UUID applicationId, Long deltaCreditUtilization) ;

    public void resetAllApplicationCredits();

    public CreditInfo getCreditInfo(UUID applicationId);
}
