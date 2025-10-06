package com.simpleAnalytics.TenetService.rpc;

import com.simpleAnalytics.protobuf.TenetProto;
import io.grpc.stub.StreamObserver;

public interface APIKeyValidationService {
    public void GetApplicationId(TenetProto.APIKeyValidationRequest request,
                               StreamObserver<TenetProto.APIKeyValidationResponse> responseObserver);
}
