package com.simpleAnalytics.TenetService.entity;

import jakarta.persistence.*;
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
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
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
