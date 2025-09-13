package com.simpleAnalytics.Gateway.service;

import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.UserEvent;

public interface EventPipelineService {

    void processEvent(UserEvent event, Context context);
}
