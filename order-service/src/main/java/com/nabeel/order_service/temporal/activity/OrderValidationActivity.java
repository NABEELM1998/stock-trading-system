package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.exceptions.ValidationException;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderValidationActivity {
    void validateOrder(WorkflowRequest request) throws ValidationException;

}

