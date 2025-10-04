package com.simpleAnalytics.Gateway.rpc.impl;


import com.simpleAnalytics.Gateway.rpc.APIKeyService;
import com.simpleAnalytics.protobuf.APIKeyServiceGrpc;
import com.simpleAnalytics.protobuf.APIKeyValidationProto.APIKeyValidationRequest;
import com.simpleAnalytics.protobuf.APIKeyValidationProto.APIKeyValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class APIKeyServiceImpl implements APIKeyService {

    @GrpcClient("apikey-service")
    private APIKeyServiceGrpc.APIKeyServiceBlockingStub stub;

    public APIKeyServiceImpl(APIKeyServiceGrpc.APIKeyServiceBlockingStub stub) {
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
