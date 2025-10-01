package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.dto.TenetDTO;
import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.repository.TenetRepository;
import com.simpleAnalytics.TenetService.service.TenetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TenetServiceImpl implements TenetService {
    private final TenetRepository tenetRepository;

    @Override
    public UUID createTenet(TenetDTO tenet) {
        Tenet tenetEntity = Tenet.builder()
                .id(UUID.randomUUID())
                .applicationsList(new ArrayList<>())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .name(tenet.getName())
                .plan(tenet.getPlan())
                .build();
        tenetRepository.save(tenetEntity);
        return tenetEntity.getId();
    }

    @Override
    public TenetDTO getTenet(UUID id) {
        Optional<Tenet> tenet = tenetRepository.findById(id);
        return tenet.map(TenetDTO::new).orElse(null);
    }

    @Override
    public void updateTenet(UUID id, Tenet updatedTenet) {

    }

    @Override
    public void deleteTenet(UUID id) {

    }

    @Override
    public Optional<Plan> getPlan(UUID id) {
        return tenetRepository.findPlanById(id);
    }

    @Override
    public Optional<Long> getPlanCreditLimit(UUID id) {
        return tenetRepository.findPlanCreditLimitById(id);
    }
}
