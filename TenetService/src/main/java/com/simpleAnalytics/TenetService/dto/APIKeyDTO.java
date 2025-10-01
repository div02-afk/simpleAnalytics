package com.simpleAnalytics.TenetService.dto;

import com.simpleAnalytics.TenetService.entity.APIKey;
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
public class APIKeyDTO {

    private UUID id;
    private String name;
    private Timestamp createdAt;
    private UUID applicationId;

    public APIKeyDTO(APIKey apiKey) {
        this.id = apiKey.getId();
        this.name = apiKey.getName();
        this.createdAt = apiKey.getCreatedAt();
        this.applicationId = apiKey.getApplication() != null ? apiKey.getApplication().getId() : null;
    }

    public static APIKeyDTO fromEntity(APIKey apiKey) {
        return apiKey != null ? new APIKeyDTO(apiKey) : null;
    }
}
