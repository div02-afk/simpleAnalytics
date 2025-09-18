package com.simpleAnalytics.TenetService.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class APIKey {
    final UUID id;
    final String name;
    final Timestamp createdAt;
}
