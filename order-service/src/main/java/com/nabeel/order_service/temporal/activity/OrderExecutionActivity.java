package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.ExecutionResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.exceptions.OrderExecutionException;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderExecutionActivity {
    @ActivityMethod
    ExecutionResult executeOrder(WorkflowRequest request) throws OrderExecutionException;

}

