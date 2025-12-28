package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.FraudCheckResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.exceptions.FraudCheckFailedException;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FraudCheckActivity {
    @ActivityMethod
    FraudCheckResult performFraudCheck(WorkflowRequest request) throws FraudCheckFailedException;

}

