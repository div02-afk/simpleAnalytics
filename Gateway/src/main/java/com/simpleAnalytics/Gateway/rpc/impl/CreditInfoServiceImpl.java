package com.simpleAnalytics.Gateway.rpc.impl;


import com.simpleAnalytics.Gateway.entity.CreditInfo;
import com.simpleAnalytics.Gateway.rpc.CreditInfoService;
import com.simpleAnalytics.protobuf.TenetProto;
import com.simpleAnalytics.protobuf.TenetServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditInfoServiceImpl implements CreditInfoService {

    private TenetServiceGrpc.TenetServiceBlockingStub stub;

    CreditInfoServiceImpl(TenetServiceGrpc.TenetServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public CreditInfo getCreditInfo(UUID applicationId) {
        log.info("Fetching Credit Info for Application Id: {}", applicationId);
        TenetProto.ApplicationCreditInfoRequest request = TenetProto.ApplicationCreditInfoRequest.newBuilder()
                .setApplicationId(applicationId.toString())
                .build();

        TenetProto.ApplicationCreditInfoResponse response = stub.getApplicationCreditInfo(request);

        return new CreditInfo(response.getCreditLimit(), response.getCreditUtilization());
    }
}
