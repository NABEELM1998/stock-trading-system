package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.ValidationResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderValidationActivity {
    @ActivityMethod
    ValidationResult validateOrder(CreateOrderRequest request);

}

