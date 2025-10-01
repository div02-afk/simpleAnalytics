package com.simpleAnalytics.TenetService.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIKey {
    @Id
     UUID id;
     String name;
     Timestamp createdAt;
}
