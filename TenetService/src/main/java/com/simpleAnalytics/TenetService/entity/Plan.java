package com.simpleAnalytics.TenetService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    UUID id;
    String name;
    long monthlyCreditLimit;
    int rateLimit;
    Date startDate;
    Duration duration;
    int cost;

    public Date getEndDate() {
        Date endDate = new Date();
        endDate.setTime(startDate.getTime() + duration.toMillis());
        return endDate;
    }
}
