package com.simpleAnalytics.TenetService.dto;

import com.simpleAnalytics.TenetService.entity.Tenet;
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
public class TenetDTO {

    private UUID id;
    private String name;
    private Timestamp createdAt;
    private PlanDTO plan;

    public TenetDTO(Tenet tenet) {
        this.id = tenet.getId();
        this.name = tenet.getName();
        this.createdAt = tenet.getCreatedAt();
        this.plan = PlanDTO.fromEntity(tenet.getPlan());
    }

    public static TenetDTO fromEntity(Tenet tenet) {
        return tenet != null ? new TenetDTO(tenet) : null;
    }
}
