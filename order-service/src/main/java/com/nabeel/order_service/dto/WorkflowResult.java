package com.nabeel.order_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
public class WorkflowResult {
    private boolean success;
    private String status;
    private String message;
    private BigDecimal executionPrice;
    private Integer filledQuantity;
    private BigDecimal fees;

    public WorkflowResult() {
    }

    public WorkflowResult(boolean success, String status, String message,
                          BigDecimal executionPrice, Integer filledQuantity, BigDecimal fees) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.executionPrice = executionPrice;
        this.filledQuantity = filledQuantity;
        this.fees = fees;
    }

}
