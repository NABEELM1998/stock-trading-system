package com.nabeel.order_service.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderExecutionActivity {
    @ActivityMethod
    ExecutionResult executeOrder(ExecutionRequest request);

    class ExecutionRequest {
        private Long orderId;
        private String symbol;
        private String side;
        private Integer quantity;
        private String orderType;
        private Double limitPrice;

        public ExecutionRequest() {}

        public ExecutionRequest(Long orderId, String symbol, String side, Integer quantity, 
                               String orderType, Double limitPrice) {
            this.orderId = orderId;
            this.symbol = symbol;
            this.side = side;
            this.quantity = quantity;
            this.orderType = orderType;
            this.limitPrice = limitPrice;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public String getSide() { return side; }
        public void setSide(String side) { this.side = side; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getOrderType() { return orderType; }
        public void setOrderType(String orderType) { this.orderType = orderType; }
        public Double getLimitPrice() { return limitPrice; }
        public void setLimitPrice(Double limitPrice) { this.limitPrice = limitPrice; }
    }

    class ExecutionResult {
        private boolean executed;
        private Double executionPrice;
        private Integer filledQuantity;
        private Double fees;
        private String message;

        public ExecutionResult() {}

        public ExecutionResult(boolean executed, Double executionPrice, Integer filledQuantity, 
                              Double fees, String message) {
            this.executed = executed;
            this.executionPrice = executionPrice;
            this.filledQuantity = filledQuantity;
            this.fees = fees;
            this.message = message;
        }

        public boolean isExecuted() { return executed; }
        public void setExecuted(boolean executed) { this.executed = executed; }
        public Double getExecutionPrice() { return executionPrice; }
        public void setExecutionPrice(Double executionPrice) { this.executionPrice = executionPrice; }
        public Integer getFilledQuantity() { return filledQuantity; }
        public void setFilledQuantity(Integer filledQuantity) { this.filledQuantity = filledQuantity; }
        public Double getFees() { return fees; }
        public void setFees(Double fees) { this.fees = fees; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

