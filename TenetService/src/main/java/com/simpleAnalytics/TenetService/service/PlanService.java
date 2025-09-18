package com.simpleAnalytics.TenetService.service;

import com.simpleAnalytics.TenetService.entity.Plan;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    public Plan getPlan(UUID id);
    public List<Plan> getAllPlans();
    public UUID createPlan(Plan plan);
    public void deletePlan(UUID id);
}
