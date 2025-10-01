package com.simpleAnalytics.Gateway.cache.impl;

import com.simpleAnalytics.Gateway.cache.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreditServiceImpl implements CreditService {
    private final RedisTemplate<String, Long> redisTemplate;


    @Override
    public long getCreditLimit(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:creditLimit:" + appId.toString());
        try {
            return creditLimit == null ? 0 : creditLimit;
        } catch (Exception e) {
            log.info("credit limit not found");
            return -1;
        }
    }

    @Override
    public long getCreditUtilization(UUID appId) {
        Long creditLimit = redisTemplate.opsForValue().get("app:creditUtilization:" + appId.toString());
        return creditLimit == null ? 0 : creditLimit;
    }
}
