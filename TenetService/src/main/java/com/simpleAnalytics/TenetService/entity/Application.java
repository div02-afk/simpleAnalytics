package com.simpleAnalytics.TenetService.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@Builder
@Data
@RequiredArgsConstructor
public class Application {
    final UUID id;
    final String name;
    final List<APIKey> apiKeysList;
    final String source;
    final Timestamp createdAt;
    final int creditsUsed;
}
