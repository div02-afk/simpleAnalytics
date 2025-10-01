package com.simpleAnalytics.TenetService.controller;

import com.simpleAnalytics.TenetService.dto.PlanDTO;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.exception.PlanNotFoundException;
import com.simpleAnalytics.TenetService.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<UUID> createPlan(@RequestBody Plan plan) {
        try {
            UUID planId = planService.createPlan(plan);
            log.info("Created plan with ID: {}", planId);
            return ResponseEntity.status(HttpStatus.CREATED).body(planId);
        } catch (Exception e) {
            log.error("Error creating plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable UUID id) {
        try {
            Plan plan = planService.getPlan(id);
            if (plan != null) {
                PlanDTO planDTO = PlanDTO.fromEntity(plan);
                return ResponseEntity.ok(planDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (PlanNotFoundException e) {
            log.error("Plan not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        try {
            List<Plan> plans = planService.getAllPlans();
            List<PlanDTO> planDTOs = plans.stream()
                    .map(PlanDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(planDTOs);
        } catch (Exception e) {
            log.error("Error retrieving all plans: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        try {
            planService.deletePlan(id);
            log.info("Deleted plan with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (PlanNotFoundException e) {
            log.error("Plan not found for deletion: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
