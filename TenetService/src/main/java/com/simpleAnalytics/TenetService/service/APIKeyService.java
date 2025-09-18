package com.simpleAnalytics.TenetService.service;

import com.simpleAnalytics.TenetService.entity.APIKey;

import java.util.UUID;

public interface APIKeyService {
    public APIKey createAPIKey(UUID appId, String APIKeyName);
    public APIKey getAPIKey(UUID id);
    public void deleteAPIKey(UUID id);
}
