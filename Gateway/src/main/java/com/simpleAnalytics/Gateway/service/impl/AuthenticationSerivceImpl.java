package com.simpleAnalytics.Gateway.service.impl;

import com.simpleAnalytics.Gateway.cache.APIKeyValidityCheck;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;
import com.simpleAnalytics.Gateway.rpc.APIKeyService;
import com.simpleAnalytics.Gateway.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationSerivceImpl implements AuthenticationService {

    private final APIKeyService apiKeyService;
    private final APIKeyValidityCheck apiKeyValidityCheck;

    @Override
    public void authenticate(UUID apiKey, UUID applicationId) throws InvalidAPIKeyException {

//        log.info("API Key Validity Check Started");
        String appIdFromCache = apiKeyValidityCheck.getCachedAPIKeyApplicationId(apiKey);

        if (appIdFromCache == null) {
            log.info("Cache miss");
            //If cache is null, get from tenet-service
            String appIdFromTenetService = apiKeyService.getApplicationIdForAPIKey(apiKey);
            UUID applicationIdFromTenetService = UUID.fromString(appIdFromTenetService);
            if (!applicationIdFromTenetService.equals(applicationId)) {
                log.info("API Key Validity Check Failed");
                throw new InvalidAPIKeyException(apiKey.toString());
            }
            log.info("Caching apikey");
            //cache the verified value if not already present
            apiKeyValidityCheck.cacheAPIKeyApplicationId(apiKey, applicationId);
        } else {
//            log.info("Cache hit");
            UUID applicationIdFromCache = UUID.fromString(appIdFromCache);
            if (!applicationIdFromCache.equals(applicationId)) {
                log.info("API Key Validity Check Failed");
                throw new InvalidAPIKeyException(apiKey.toString());
            }
        }
    }
}
