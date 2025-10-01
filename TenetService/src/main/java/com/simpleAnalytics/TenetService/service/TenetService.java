package com.simpleAnalytics.TenetService.service;

import com.simpleAnalytics.TenetService.dto.TenetDTO;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;

import java.util.Optional;
import java.util.UUID;

public interface TenetService {

    public UUID createTenet(TenetDTO tenet);

    public TenetDTO getTenet(UUID id);

    public void updateTenet(UUID id, Tenet updatedTenet);

    public void deleteTenet(UUID id);

    public void setPlan(UUID tenetId, UUID planId);

    public Optional<Plan> getPlan(UUID id);

    public Optional<Long> getPlanCreditLimit(UUID id);
}
