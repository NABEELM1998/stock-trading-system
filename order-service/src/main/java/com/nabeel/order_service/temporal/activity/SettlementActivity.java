package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.SettlementResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SettlementActivity {
    @ActivityMethod
    SettlementResult settleOrder(WorkflowRequest request);

    @ActivityMethod
    void compensateSettlement(WorkflowRequest request);

}

