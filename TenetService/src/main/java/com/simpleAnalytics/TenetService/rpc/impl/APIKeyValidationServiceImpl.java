package com.simpleAnalytics.TenetService.rpc.impl;


import com.simpleAnalytics.TenetService.entity.CreditInfo;
import com.simpleAnalytics.TenetService.service.APIKeyService;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import com.simpleAnalytics.protobuf.TenetProto;
import com.simpleAnalytics.protobuf.TenetProto;
import com.simpleAnalytics.protobuf.TenetServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class APIKeyValidationServiceImpl extends TenetServiceGrpc.TenetServiceImplBase {
    private final APIKeyService apiKeyService;
    private final ApplicationService applicationService;

    @Override
    public void getApplicationId(TenetProto.APIKeyValidationRequest request,
                                 StreamObserver<TenetProto.APIKeyValidationResponse> responseObserver) {

        Optional<UUID> applicationId = apiKeyService.getApplicationIdForAPIKey(UUID.fromString(request.getApikey()));
        TenetProto.APIKeyValidationResponse response = TenetProto.APIKeyValidationResponse.newBuilder()
                .setApplicationId(applicationId.map(UUID::toString).orElse(null))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getApplicationCreditInfo(TenetProto.ApplicationCreditInfoRequest request,
                                         StreamObserver<TenetProto.ApplicationCreditInfoResponse> responseObserver) {
        try {

            CreditInfo creditInfo = applicationService.getCreditInfo(UUID.fromString(request.getApplicationId()));
            TenetProto.ApplicationCreditInfoResponse response = TenetProto.ApplicationCreditInfoResponse.newBuilder()
                    .setCreditLimit(creditInfo.creditLimit())
                    .setCreditUtilization(creditInfo.creditsUsed())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }

    }

}
