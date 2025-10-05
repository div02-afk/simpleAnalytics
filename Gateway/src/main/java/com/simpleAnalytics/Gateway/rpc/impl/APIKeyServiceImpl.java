package com.simpleAnalytics.Gateway.rpc.impl;


import com.simpleAnalytics.Gateway.rpc.APIKeyService;
import com.simpleAnalytics.protobuf.TenetServiceGrpc;
import com.simpleAnalytics.protobuf.TenetProto.APIKeyValidationRequest;
import com.simpleAnalytics.protobuf.TenetProto.APIKeyValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class APIKeyServiceImpl implements APIKeyService {

    @GrpcClient("tenet-service")
    private TenetServiceGrpc.TenetServiceBlockingStub stub;

    public APIKeyServiceImpl(TenetServiceGrpc.TenetServiceBlockingStub stub) {
        this.stub = stub;
    }


    @Override
    public String getApplicationIdForAPIKey(UUID apiKey) {
        APIKeyValidationRequest request = APIKeyValidationRequest.newBuilder()
                .setApikey(apiKey.toString())
                .build();
        log.info("API Key Validation Request: {}", request);
        APIKeyValidationResponse apiKeyValidationResponse = stub.getApplicationId(request);
        log.info("API Key Validation Response: {}", apiKeyValidationResponse.getApplicationId());
        return (apiKeyValidationResponse.getApplicationId());
    }


}
