package com.simpleAnalytics.Gateway.service;

import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;

import java.util.UUID;

public interface AuthenticationService {

    public void authenticate(UUID apikey, UUID applicationId) throws InvalidAPIKeyException;
}
