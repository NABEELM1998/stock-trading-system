package com.nabeel.order_service.temporal.workflow;

import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.dto.WorkflowResult;
import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.exceptions.FraudCheckFailedException;
import com.nabeel.order_service.exceptions.OrderExecutionException;
import com.nabeel.order_service.exceptions.ValidationException;
import com.nabeel.order_service.temporal.activity.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Saga;
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
    private final ActivityOptions orderStatusUpdateActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();

    private final OrderStatusUpdateActivity orderStatusUpdateActivity =
            Workflow.newActivityStub(OrderStatusUpdateActivity.class, orderStatusUpdateActivityOptions);
    private final ActivityOptions settlementCompensationActivityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();

    private final SettlementActivity settlementCompensation  =
            Workflow.newActivityStub(SettlementActivity.class, settlementCompensationActivityOptions);

    Saga.Options sagaOptions = new Saga.Options.Builder()
            .setParallelCompensation(false)
            .build();

    Saga saga = new Saga(sagaOptions);



    @Override
    public WorkflowResult executeOrder(WorkflowRequest request) {
        try{
            LOGGER.info("validating order request");
            orderValidationActivity.validateOrder(request);
            LOGGER.info("performing fraud checks");
            fraudCheckActivity.performFraudCheck(request);
            LOGGER.info("proceeding with order execution");
            OrderExecutionActivity.executeOrder(request);
            LOGGER.info("proceeding with settlement");
            settlementActivity.settleOrder(request);
            saga.addCompensation(() -> settlementCompensation.compensateSettlement(request));
        }catch (ActivityFailure e){
            LOGGER.error("activity failure {}",e.getMessage());
            ApplicationFailure cause = (ApplicationFailure) e.getCause();

            if( cause.getType().contains("ValidationException")){
                LOGGER.error("Order validation failed moving order to Failed");
                orderStatusUpdateActivity.updateOrderStatus(request.getOrderId(),Order.OrderStatus.FAILED.name(), "validation failed");
            }
            if( cause.getType().contains("FraudCheckFailedException")){
                LOGGER.error("Fraud check failed moving order to Failed");
                orderStatusUpdateActivity.updateOrderStatus(request.getOrderId(),Order.OrderStatus.FAILED.name(), "fraud-check failed");
            }
            if(cause.getType().contains("OrderExecutionException")){
                LOGGER.error("Order Execution failed moving order to Rejected");
                orderStatusUpdateActivity.updateOrderStatus(request.getOrderId(),Order.OrderStatus.REJECTED.name(), "Order execution failed");
            }
            saga.compensate();


        }
        return new WorkflowResult(true, Order.OrderStatus.FILLED.name(),"success",request.getExecutionPrice(), request.getQuantity(), request.getFees());
        }
    }



