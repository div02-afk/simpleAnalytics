package com.simpleAnalytics.TenetService.entity;


import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Tenet {
    UUID id;
    String name;
    List<Application> applicationsList;
    Plan plan;
    Timestamp createdAt;
    public int totalCreditsUsed(){
        return applicationsList.stream().mapToInt(Application::getCreditsUsed).sum();
    }
}
