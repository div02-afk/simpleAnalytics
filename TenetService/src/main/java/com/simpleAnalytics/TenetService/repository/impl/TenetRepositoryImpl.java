package com.simpleAnalytics.TenetService.repository.impl;

import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.repository.TenetRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Repository
public class TenetRepositoryImpl implements TenetRepository {
    @Override
    public void createTenet(Tenet tenet) {

    }

    @Override
    public Tenet getTenet(UUID id) {
        return null;
    }
}
