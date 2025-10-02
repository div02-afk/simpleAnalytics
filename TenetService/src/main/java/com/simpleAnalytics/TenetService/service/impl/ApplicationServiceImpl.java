package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.cache.CreditService;
import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.exception.ApplicationNotFoundException;
import com.simpleAnalytics.TenetService.exception.TenetNotFoundException;
import com.simpleAnalytics.TenetService.repository.ApplicationRepository;
import com.simpleAnalytics.TenetService.repository.TenetRepository;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import com.simpleAnalytics.TenetService.service.TenetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TenetRepository tenetRepository;
    private final TenetService tenetService;
    private final CreditService creditService;

    @Override
    public UUID createApplication(UUID tenetId, Application application) {
        log.debug("Creating application for tenet: {}", tenetId);

        // Verify tenet exists
        Optional<Tenet> tenetOpt = tenetRepository.findById(tenetId);
        if (tenetOpt.isEmpty()) {
            log.error("Tenet not found with ID: {}", tenetId);
            throw new TenetNotFoundException(tenetId);
        }

        // Set up application
//        application.setId(UUID.randomUUID());
        application.setTenet(tenetOpt.get());
        application.setCreatedAt(Timestamp.from(Instant.now()));
        application.setApiKeysList(null);
        // Get plan credit limit
        Optional<Long> optionalPlan = tenetService.getPlanCreditLimit(tenetId);
        if (optionalPlan.isPresent()) {
            try {
                Application savedApplication = applicationRepository.save(application);
                creditService.setAppLimit(application.getId(), optionalPlan.get());
                log.info("Application created successfully with ID: {} for tenet: {}", savedApplication.getId(), tenetId);
                return savedApplication.getId();
            } catch (Exception e) {
                log.error("Error saving application: {}", e.getMessage());
                throw new RuntimeException("Failed to create application", e);
            }
        } else {
            log.error("No plan found for tenet: {}", tenetId);
            throw new RuntimeException("Cannot create application: Tenet has no plan configured");
        }
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


    //TODO add history for past usage
    @Scheduled(cron = "0 0 0 1 * *")
    @Override
    @Transactional
    @Retryable(retryFor =  Exception.class)
    public void resetAllApplicationCredits() {
        log.info("Resetting application credits for tenet");
        applicationRepository.resetCredits();
    }

}
