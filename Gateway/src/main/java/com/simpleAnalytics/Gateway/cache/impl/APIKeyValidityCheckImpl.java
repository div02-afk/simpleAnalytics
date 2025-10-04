package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.APIKeyValidityCheck;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;
import com.simpleAnalytics.Gateway.rpc.APIKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class APIKeyValidityCheckImpl implements APIKeyValidityCheck {
    private final RedisTemplate<String, String> redisTemplate;
    private final APIKeyService apiKeyService;

    @Override
    public void checkAPIKeyValidity(UUID apiKey, UUID claimedApplicationId) throws InvalidAPIKeyException {
        log.info("API Key Validity Check Started");
        String appIdFromCache = getCachedAPIKeyApplicationId(apiKey);

        if (appIdFromCache == null) {
            log.info("Cache miss");
            //If cache is null, get from tenet-service
            String appIdFromTenetService = apiKeyService.getApplicationIdForAPIKey(apiKey);
            if (!appIdFromTenetService.equals(claimedApplicationId.toString())) {
                log.info("API Key Validity Check Failed");
                throw new InvalidAPIKeyException(apiKey.toString());
            }
            log.info("Caching apikey");
            //cache the verified value if not already present
            cacheAPIKeyApplicationId(apiKey, claimedApplicationId);
        } else {
            log.info("Cache hit");
            if (!appIdFromCache.equals(claimedApplicationId.toString())) {
                log.info("API Key Validity Check Failed");
                throw new InvalidAPIKeyException(apiKey.toString());
            }
        }
    }


    @Override
    public void cacheAPIKeyApplicationId(UUID apikey, UUID applicationId) {
        redisTemplate.opsForValue().set("apikey:" + apikey.toString(), applicationId.toString());
    }


    //TODO add cache invalidation/TTL
    @Override
    public String getCachedAPIKeyApplicationId(UUID apikey) {
        return redisTemplate.opsForValue().get("apikey:" + apikey.toString());
    }
}
