package com.simpleAnalytics.Gateway.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@Data
@RequiredArgsConstructor
public class APIKeyInfo {
    UUID apiKey;
    UUID appId;
    APIKeyStatus apiKeyStatus;
}
