package com.nabeel.order_service.dto;

public class SettlementRequest {
    private Long orderId;
    private Long userId;
    private String side;
    private Integer quantity;
    private Double executionPrice;
    private Double fees;

    public SettlementRequest() {
    }

    public SettlementRequest(Long orderId, Long userId, String side, Integer quantity,
                             Double executionPrice, Double fees) {
        this.orderId = orderId;
        this.userId = userId;
        this.side = side;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
        this.fees = fees;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }
}
