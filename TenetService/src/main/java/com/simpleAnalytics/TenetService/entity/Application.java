package com.simpleAnalytics.TenetService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
     UUID id;
     String name;
     List<APIKey> apiKeysList;
     String source;
     Timestamp createdAt;
     long creditsUsed;
}
