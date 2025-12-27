package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.ExecutionResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderExecutionActivity {
    @ActivityMethod
    ExecutionResult executeOrder(CreateOrderRequest request);

}

