package com.simpleAnalytics.TenetService.rpc.impl;


import com.simpleAnalytics.TenetService.service.APIKeyService;
import com.simpleAnalytics.protobuf.APIKeyServiceGrpc;
import com.simpleAnalytics.protobuf.APIKeyValidationProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class APIKeyValdiationServiceImpl extends APIKeyServiceGrpc.APIKeyServiceImplBase {
    private final APIKeyService apiKeyService;

    @Override
    public void getApplicationId(APIKeyValidationProto.APIKeyValidationRequest request,
                               StreamObserver<APIKeyValidationProto.APIKeyValidationResponse> responseObserver) {

        Optional<UUID> applicationId = apiKeyService.getApplicationIdForAPIKey(UUID.fromString(request.getApikey()));
        APIKeyValidationProto.APIKeyValidationResponse response = APIKeyValidationProto.APIKeyValidationResponse.newBuilder()
                .setApplicationId(applicationId.map(UUID::toString).orElse(null))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
