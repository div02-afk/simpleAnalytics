package com.simpleAnalytics.Gateway.service;

import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.UserEvent;

import java.util.UUID;

public interface EventPipelineService {

    void processEvent(UserEvent event, UUID apiKey, Context context) throws Exception;
}
