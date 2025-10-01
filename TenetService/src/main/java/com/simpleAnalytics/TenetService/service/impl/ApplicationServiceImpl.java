package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.cache.CreditService;
import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.exception.ApplicationNotFoundException;
import com.simpleAnalytics.TenetService.repository.ApplicationRepository;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import com.simpleAnalytics.TenetService.service.TenetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TenetService tenetService;
    private final CreditService creditService;
    @Override
    public UUID createApplication(UUID tenetId, Application application) {
        application.setId(UUID.randomUUID());
        Optional<Long> optionalPlan =  tenetService.getPlanCreditLimit(tenetId);
        if(optionalPlan.isPresent()){
            try {
                Application savedApplication = applicationRepository.save(application);
                creditService.setAppLimit(application.getId(),optionalPlan.get());
                return savedApplication.getId();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Application getApplication(UUID id) {
        Optional<Application> application = applicationRepository.findById(id);
        return application.orElse(null);
    }

    @Override
    public List<Application> getAllApplications(UUID tenetId) {
        return List.of();
    }

    @Override
    public void updateApplication(UUID id, Application application) {
        Optional<Application> existingApplicationOpt = applicationRepository.findById(id);
        if (existingApplicationOpt.isPresent()) {
            Application existingApplication = existingApplicationOpt.get();

            // Update fields if they are provided in the input application
            if (application.getName() != null) {
                existingApplication.setName(application.getName());
            }

            if (application.getApiKeysList() != null) {
                existingApplication.setApiKeysList(application.getApiKeysList());
            }

            if (application.getSource() != null) {
                existingApplication.setSource(application.getSource());
            }

            // Note: Not updating createdAt as it should remain immutable
            // Note: Not updating creditsUsed directly as it should be managed through credit operations
            // Save the updated application
            applicationRepository.save(existingApplication);
            log.info("Application updated successfully with ID: {}", id);

        } else {
            log.error("Application not found with ID: {}", id);
            throw new ApplicationNotFoundException(id);
        }
    }


    @Override
    @Transactional
    public void incrementCredits(UUID applicationId, Long deltaCreditUtilization) {
        applicationRepository.incrementCredits(applicationId, deltaCreditUtilization);
    }

}
