package com.simpleAnalytics.TenetService.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.UUID;


@Data
public class ApplicationDTO {
    @Nullable
    UUID id;
    String name;
    String source;
    @Nullable
    Timestamp createdAt;
    int creditsUsed;
}
