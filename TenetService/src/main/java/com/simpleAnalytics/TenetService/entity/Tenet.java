package com.simpleAnalytics.TenetService.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Tenet {
    @Id
    UUID id;
    String name;
    List<Application> applicationsList;
    @ManyToOne
    Plan plan;
    Timestamp createdAt;

}
