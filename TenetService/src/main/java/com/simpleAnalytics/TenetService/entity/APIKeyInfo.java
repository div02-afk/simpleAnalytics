package com.simpleAnalytics.TenetService.entity;

import com.simpleAnalytics.Gateway.entity.APIKeyStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@Data
@RequiredArgsConstructor
public class APIKeyInfo {
    UUID apiKey;
    UUID appId;
    APIKeyStatus apiKeyStatus;
}
