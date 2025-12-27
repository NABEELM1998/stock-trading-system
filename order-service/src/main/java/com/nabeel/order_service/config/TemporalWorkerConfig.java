package com.nabeel.order_service.config;

import com.nabeel.order_service.temporal.activity.*;
import com.nabeel.order_service.temporal.activity.OrderStatusUpdateActivityImpl;
import com.nabeel.order_service.temporal.workflow.TradeOrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class TemporalWorkerConfig {

    @Value("${temporal.worker.task-queue:order-processing}")
    private String taskQueue;

    private final WorkflowClient workflowClient;
    private final OrderValidationActivityImpl validationActivity;
    private final FraudCheckActivityImpl fraudCheckActivity;
    private final OrderExecutionActivityImpl executionActivity;
    private final SettlementActivityImpl settlementActivity;
    private final OrderStatusUpdateActivityImpl statusUpdateActivity;
    private WorkerFactory workerFactory;

    public TemporalWorkerConfig(
            WorkflowClient workflowClient,
            OrderValidationActivityImpl validationActivity,
            FraudCheckActivityImpl fraudCheckActivity,
            OrderExecutionActivityImpl executionActivity,
            SettlementActivityImpl settlementActivity,
            OrderStatusUpdateActivityImpl statusUpdateActivity) {
        this.workflowClient = workflowClient;
        this.validationActivity = validationActivity;
        this.fraudCheckActivity = fraudCheckActivity;
        this.executionActivity = executionActivity;
        this.settlementActivity = settlementActivity;
        this.statusUpdateActivity = statusUpdateActivity;
    }

    @PostConstruct
    public void startWorker() {
        workerFactory = WorkerFactory.newInstance(workflowClient);
        Worker worker = workerFactory.newWorker(taskQueue);

        // Register workflow implementation
        worker.registerWorkflowImplementationTypes(TradeOrderWorkflow.class);

        // Register activity implementations
        worker.registerActivitiesImplementations(
                validationActivity,
                fraudCheckActivity,
                executionActivity,
                settlementActivity,
                statusUpdateActivity
        );

        workerFactory.start();
    }

    @PreDestroy
    public void stopWorker() {
        if (workerFactory != null) {
            workerFactory.shutdown();
        }
    }
}

