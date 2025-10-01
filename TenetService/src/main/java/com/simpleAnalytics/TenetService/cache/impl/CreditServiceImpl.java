package com.simpleAnalytics.TenetService.cache.impl;

import com.simpleAnalytics.TenetService.cache.CreditService;
import com.simpleAnalytics.TenetService.entity.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public void setAppLimit(UUID appId, long limit) {
        redisTemplate.opsForValue().set("app:creditLimit:" + appId.toString(), limit);
    }

    @Override
    public long getAppCreditLimit(UUID applicationId) {
        Long limit = redisTemplate.opsForValue().get("app:creditLimit:" + applicationId.toString());
        return limit != null ? limit : -1;
    }

    @Override
    public long getAppCreditUtilization(UUID applicationId) {
        Long limit = redisTemplate.opsForValue().get("app:creditUtilization:" + applicationId.toString());
        return limit != null ? limit : -1;
    }
}
