package com.nabeel.order_service.temporal.workflow;

import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.dto.WorkflowResult;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TradeOrderWorkflow {
    @WorkflowMethod
    WorkflowResult executeOrder(WorkflowRequest request);


}

