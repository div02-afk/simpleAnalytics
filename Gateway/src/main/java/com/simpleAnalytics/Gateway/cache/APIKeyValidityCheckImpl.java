package com.simpleAnalytics.Gateway.cache;

import com.simpleAnalytics.Gateway.entity.APIKeyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class APIKeyValidityCheckImpl implements APIKeyValidityCheck {
    private final RedisTemplate<String, APIKeyInfo> redisTemplate;

    @Override
    public void isAPIKeyValid(UUID apiKey) {
        APIKeyInfo apiKeyInfo = redisTemplate.opsForValue().get((apiKey).toString());
        if (apiKeyInfo == null) {
            //TODO: get apikeyinfo from tenetservice

            return;
        }
        switch (apiKeyInfo.getApiKeyStatus()) {
            case APIKeyInvalid -> throw new RuntimeException("Invalid API Key");
            case CREDITS_EXHAUSTED -> throw new RuntimeException("Credit Exhausted");
        }
    }
}
