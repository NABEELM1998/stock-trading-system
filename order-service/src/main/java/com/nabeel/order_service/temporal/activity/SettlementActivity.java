package com.nabeel.order_service.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SettlementActivity {
    @ActivityMethod
    SettlementResult settleOrder(SettlementRequest request);

    @ActivityMethod
    void compensateSettlement(SettlementRequest request);

    class SettlementRequest {
        private Long orderId;
        private Long userId;
        private String side;
        private Integer quantity;
        private Double executionPrice;
        private Double fees;

        public SettlementRequest() {}

        public SettlementRequest(Long orderId, Long userId, String side, Integer quantity, 
                                Double executionPrice, Double fees) {
            this.orderId = orderId;
            this.userId = userId;
            this.side = side;
            this.quantity = quantity;
            this.executionPrice = executionPrice;
            this.fees = fees;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getSide() { return side; }
        public void setSide(String side) { this.side = side; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getExecutionPrice() { return executionPrice; }
        public void setExecutionPrice(Double executionPrice) { this.executionPrice = executionPrice; }
        public Double getFees() { return fees; }
        public void setFees(Double fees) { this.fees = fees; }
    }

    class SettlementResult {
        private boolean settled;
        private String message;

        public SettlementResult() {}

        public SettlementResult(boolean settled, String message) {
            this.settled = settled;
            this.message = message;
        }

        public boolean isSettled() { return settled; }
        public void setSettled(boolean settled) { this.settled = settled; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

