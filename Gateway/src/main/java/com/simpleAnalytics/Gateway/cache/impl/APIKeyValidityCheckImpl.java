package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.APIKeyValidityCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class APIKeyValidityCheckImpl implements APIKeyValidityCheck {
    private final RedisTemplate<String, String> redisTemplate;



    @Async
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
