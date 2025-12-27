package com.nabeel.order_service.dto;

public class WorkflowResult {
    private boolean success;
    private String status;
    private String message;
    private Double executionPrice;
    private Integer filledQuantity;
    private Double fees;

    public WorkflowResult() {
    }

    public WorkflowResult(boolean success, String status, String message,
                          Double executionPrice, Integer filledQuantity, Double fees) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.executionPrice = executionPrice;
        this.filledQuantity = filledQuantity;
        this.fees = fees;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
    }

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }
}
