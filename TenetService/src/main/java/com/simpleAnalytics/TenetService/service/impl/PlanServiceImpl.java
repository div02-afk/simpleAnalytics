package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.exception.PlanNotFoundException;
import com.simpleAnalytics.TenetService.repository.PlanRepository;
import com.simpleAnalytics.TenetService.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public Plan getPlan(UUID id) {
        log.debug("Retrieving plan with ID: {}", id);
        Optional<Plan> plan = planRepository.findById(id);
        if (plan.isPresent()) {
            log.info("Plan found with ID: {}", id);
            return plan.get();
        } else {
            log.error("Plan not found with ID: {}", id);
            throw new PlanNotFoundException(id);
        }
    }

    @Override
    public List<Plan> getAllPlans() {
        log.debug("Retrieving all plans");
        List<Plan> plans = planRepository.findAll();
        log.info("Retrieved {} plans", plans.size());
        return plans;
    }

    @Override
    public UUID createPlan(Plan plan) {
        log.debug("Creating new plan: {}", plan.getName());

        // Generate UUID if not provided
        if (plan.getId() == null) {
            plan.setId(UUID.randomUUID());
        }

        Plan savedPlan = planRepository.save(plan);
        log.info("Plan created successfully with ID: {}", savedPlan.getId());
        return savedPlan.getId();
    }

    @Override
    public void deletePlan(UUID id) {
        log.debug("Deleting plan with ID: {}", id);

        // Check if plan exists before deletion
        if (!planRepository.existsById(id)) {
            log.error("Plan not found for deletion with ID: {}", id);
            throw new PlanNotFoundException(id);
        }

        planRepository.deleteById(id);
        log.info("Plan deleted successfully with ID: {}", id);
    }
}
