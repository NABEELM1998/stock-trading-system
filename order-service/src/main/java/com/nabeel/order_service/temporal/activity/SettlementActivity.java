package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.SettlementRequest;
import com.nabeel.order_service.dto.SettlementResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SettlementActivity {
    @ActivityMethod
    SettlementResult settleOrder(CreateOrderRequest request);

    @ActivityMethod
    void compensateSettlement(SettlementRequest request);

}

