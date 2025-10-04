package com.simpleAnalytics.TenetService.rpc;

import com.simpleAnalytics.protobuf.APIKeyValidationProto;
import io.grpc.stub.StreamObserver;

public interface APIKeyValidationService {
    public void GetApplicationId(APIKeyValidationProto.APIKeyValidationRequest request,
                               StreamObserver<APIKeyValidationProto.APIKeyValidationResponse> responseObserver);
}
