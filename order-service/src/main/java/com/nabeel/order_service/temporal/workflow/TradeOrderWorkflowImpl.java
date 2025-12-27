package com.nabeel.order_service.temporal.workflow;

import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.dto.WorkflowResult;
import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.temporal.activity.FraudCheckActivity;
import com.nabeel.order_service.temporal.activity.OrderExecutionActivity;
import com.nabeel.order_service.temporal.activity.OrderValidationActivity;
import com.nabeel.order_service.temporal.activity.SettlementActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    private final ActivityOptions fraudCheckActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(2).build())
                    .build();

    private final FraudCheckActivity fraudCheckActivity =
            Workflow.newActivityStub(FraudCheckActivity.class, fraudCheckActivityOptions);

    private final ActivityOptions orderExecutionActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
                    .build();

    private final OrderExecutionActivity OrderExecutionActivity =
            Workflow.newActivityStub(OrderExecutionActivity.class, orderExecutionActivityOptions);

    private final ActivityOptions settlementActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();

    private final SettlementActivity settlementActivity =
            Workflow.newActivityStub(SettlementActivity.class, settlementActivityOptions);

    @Override
    public WorkflowResult executeOrder(WorkflowRequest request) {
        LOGGER.info("validating order request");
        orderValidationActivity.validateOrder(request);
        LOGGER.info("performing fraud checks");
        fraudCheckActivity.performFraudCheck(request);
        LOGGER.info("proceeding with order execution");
        OrderExecutionActivity.executeOrder(request);
        LOGGER.info("proceeding with settlement");
        settlementActivity.settleOrder(request);
        return new WorkflowResult(true, Order.OrderStatus.FILLED.name(),"success",request.getExecutionPrice(), request.getQuantity(), request.getFees());


        }
    }



