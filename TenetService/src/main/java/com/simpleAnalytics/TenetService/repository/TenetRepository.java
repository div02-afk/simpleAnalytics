package com.simpleAnalytics.TenetService.repository;

import com.simpleAnalytics.TenetService.entity.Tenet;

import java.util.UUID;

public interface TenetRepository {

    public void createTenet(Tenet tenet);

    public Tenet getTenet(UUID id);
}
