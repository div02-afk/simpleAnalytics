package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.dto.ApplicationDTO;
import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.entity.CreditInfo;
import com.simpleAnalytics.TenetService.exception.InsufficientCreditsException;
import com.simpleAnalytics.TenetService.repository.ApplicationRepository;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Override
    public UUID createApplication(Application application) {
        return null;
    }

    @Override
    public Application getApplication(UUID id) {
        return null;
    }

    @Override
    public List<Application> getAllApplications(UUID tenetId) {
        return List.of();
    }

    @Override
    public void updateApplication(UUID id, Application application) {
        Application existingApplication = getApplication(id);
        if (existingApplication != null) {
            log.error("Application not found");
            //TODO throw exception
            return;
        }

    }

    @Override
    public boolean canUseCredit(UUID applicationId, int creditAmount) {
        CreditInfo creditInfo = applicationRepository.getCreditInfo(applicationId);
        return creditInfo != null && creditInfo.getCreditLimit() > creditAmount + creditInfo.getCreditsUsed();
    }

    @Override
    public void useCredit(UUID applicationId, int creditAmount) throws InsufficientCreditsException {
        if (canUseCredit(applicationId, creditAmount)) {
            log.info("Sufficient credits available for application {}", applicationId);
            applicationRepository.appendCredits(applicationId, creditAmount);
        } else {
            //TODO throw exception
            log.info("Insufficient credits for application {}", applicationId);
            throw new InsufficientCreditsException(applicationId);
        }
    }
}
