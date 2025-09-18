package com.simpleAnalytics.TenetService.entity;

import lombok.*;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;


@RequiredArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Plan {
    UUID id;
    String name;
    int monthlyCreditLimit;
    int rateLimit;
    Date startDate;
    Duration duration;
    int cost;
    public Date getEndDate(){
        Date endDate = new Date();
        endDate.setTime(startDate.getTime() + duration.toMillis());
        return endDate;
    }
}
