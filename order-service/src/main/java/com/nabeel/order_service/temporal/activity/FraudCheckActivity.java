package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.FraudCheckResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FraudCheckActivity {
    @ActivityMethod
    FraudCheckResult performFraudCheck(WorkflowRequest request);

}

