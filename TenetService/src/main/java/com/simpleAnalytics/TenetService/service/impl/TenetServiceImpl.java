package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.dto.TenetDTO;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.exception.PlanNotFoundException;
import com.simpleAnalytics.TenetService.exception.TenetNotFoundException;
import com.simpleAnalytics.TenetService.repository.PlanRepository;
import com.simpleAnalytics.TenetService.repository.TenetRepository;
import com.simpleAnalytics.TenetService.service.TenetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenetServiceImpl implements TenetService {

    private final TenetRepository tenetRepository;
    private final PlanRepository planRepository;

    @Override
    public UUID createTenet(TenetDTO tenet) {
        Tenet tenetEntity = Tenet.builder()
                .id(UUID.randomUUID())
                .applicationsList(new ArrayList<>())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .name(tenet.getName())
                .plan(tenet.getPlan())
                .build();
        tenetRepository.save(tenetEntity);
        return tenetEntity.getId();
    }

    @Override
    public TenetDTO getTenet(UUID id) {
        Optional<Tenet> tenet = tenetRepository.findById(id);
        return tenet.map(TenetDTO::new).orElse(null);
    }

    @Override
    public void updateTenet(UUID id, Tenet updatedTenet) {

    }

    @Override
    public void deleteTenet(UUID id) {

    }

    @Override
    public void setPlan(UUID tenetId, UUID planId) {
        log.debug("Setting plan {} for tenet {}", planId, tenetId);

        // Verify tenet exists
        Optional<Tenet> tenetOpt = tenetRepository.findById(tenetId);
        if (tenetOpt.isEmpty()) {
            log.error("Tenet not found with ID: {}", tenetId);
            throw new TenetNotFoundException(tenetId);
        }

        // Verify plan exists
        Optional<Plan> planOpt = planRepository.findById(planId);
        if (planOpt.isEmpty()) {
            log.error("Plan not found with ID: {}", planId);
            throw new PlanNotFoundException(planId);
        }

        // Update tenet with new plan
        Tenet tenet = tenetOpt.get();
        Plan plan = planOpt.get();
        tenet.setPlan(plan);

        tenetRepository.save(tenet);
        log.info("Successfully set plan {} for tenet {}", planId, tenetId);
    }

    @Override
    public Optional<Plan> getPlan(UUID id) {
        return tenetRepository.findPlanById(id);
    }

    @Override
    public Optional<Long> getPlanCreditLimit(UUID id) {
        return tenetRepository.findPlanCreditLimitById(id);
    }
}
