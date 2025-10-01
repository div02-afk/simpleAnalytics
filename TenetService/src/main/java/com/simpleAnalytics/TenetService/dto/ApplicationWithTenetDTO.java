package com.simpleAnalytics.TenetService.dto;

import com.simpleAnalytics.TenetService.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationWithTenetDTO {

    private UUID id;
    private String name;
    private String source;
    private Timestamp createdAt;
    private long creditsUsed;
    private UUID tenetId;
    private String tenetName; // Additional context that might be useful

    public ApplicationWithTenetDTO(Application application) {
        this.id = application.getId();
        this.name = application.getName();
        this.source = application.getSource();
        this.createdAt = application.getCreatedAt();
        this.creditsUsed = application.getCreditsUsed();
        this.tenetId = application.getTenetId();
        this.tenetName = application.getTenet() != null ? application.getTenet().getName() : null;
    }

    public static ApplicationWithTenetDTO fromEntity(Application application) {
        return application != null ? new ApplicationWithTenetDTO(application) : null;
    }
}
