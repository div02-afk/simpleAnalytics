package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.dto.TenetDTO;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.repository.TenetRepository;
import com.simpleAnalytics.TenetService.service.TenetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
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
        tenetRepository.createTenet(tenetEntity);
        return tenetEntity.getId();

    }

    @Override
    public TenetDTO getTenet(UUID id) {
        Tenet tenet =  tenetRepository.getTenet(id);
        return new TenetDTO(tenet);
    }

    @Override
    public void updateTenet(UUID id, Tenet updatedTenet) {

    }

    @Override
    public void deleteTenet(UUID id) {

    }
}
