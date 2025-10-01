package com.simpleAnalytics.TenetService.dto;

import com.simpleAnalytics.TenetService.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    private UUID id;
    private String name;
    private long monthlyCreditLimit;
    private int rateLimit;
    private Date startDate;
    private Duration duration;
    private int cost;
    private Date endDate;

    public PlanDTO(Plan plan) {
        this.id = plan.getId();
        this.name = plan.getName();
        this.monthlyCreditLimit = plan.getMonthlyCreditLimit();
        this.rateLimit = plan.getRateLimit();
        this.startDate = plan.getStartDate();
        this.duration = plan.getDuration();
        this.cost = plan.getCost();
        this.endDate = plan.getEndDate();
    }

    public static PlanDTO fromEntity(Plan plan) {
        return plan != null ? new PlanDTO(plan) : null;
    }
}
