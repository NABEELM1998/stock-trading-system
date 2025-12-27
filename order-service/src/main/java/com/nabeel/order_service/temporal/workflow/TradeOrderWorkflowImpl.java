package com.nabeel.order_service.temporal.workflow;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.dto.WorkflowResult;
import com.nabeel.order_service.temporal.activity.OrderValidationActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class TradeOrderWorkflowImpl implements TradeOrderWorkflow{
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeOrderWorkflowImpl.class);
    //order validation activity
    private final ActivityOptions validationActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build();

    private final OrderValidationActivity orderValidationActivity =
            Workflow.newActivityStub(OrderValidationActivity.class, validationActivityOptions);

    @Override
    public WorkflowResult executeOrder(CreateOrderRequest request) {
        LOGGER.info("validating order request");
        orderValidationActivity.validateOrder(request);

    }


}
