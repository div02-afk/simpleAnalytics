package com.simpleAnalytics.TenetService.dto;

import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;


@Data
public class TenetDTO {
    @Nullable
    UUID id;
    String name;
    @Nullable
    List<Application> applicationList;
    Plan plan;

    public TenetDTO(Tenet tenet) {
        this.id = tenet.getId();
        this.applicationList = tenet.getApplicationsList();
        this.name = tenet.getName();
        this.plan = tenet.getPlan();
    }

}
