package com.simpleAnalytics.TenetService.service.impl;

import com.simpleAnalytics.TenetService.entity.APIKey;
import com.simpleAnalytics.TenetService.repository.APIKeyRepository;
import com.simpleAnalytics.TenetService.service.APIKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class APIKeyServiceImpl implements APIKeyService {

    private final APIKeyRepository apiKeyRepository;

    @Override
    public APIKey createAPIKey(UUID appId, String APIKeyName) {
        return null;
    }

    @Override
    public APIKey getAPIKey(UUID id) {
        return null;
    }

    @Override
    public void deleteAPIKey(UUID id) {

    }

    @Override
    public Optional<UUID> getApplicationIdForAPIKey(UUID apiKey) {
        try {
            return apiKeyRepository.findApplicationIdById(apiKey);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
