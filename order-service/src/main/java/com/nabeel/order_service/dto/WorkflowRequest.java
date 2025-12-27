package com.nabeel.order_service.dto;

public class WorkflowRequest {
    private Long orderId;
    private Long userId;
    private String symbol;
    private String side;
    private Integer quantity;
    private String orderType;
    private Double limitPrice;

    public WorkflowRequest() {
    }

    public WorkflowRequest(Long orderId, Long userId, String symbol, String side,
                           Integer quantity, String orderType, Double limitPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.orderType = orderType;
        this.limitPrice = limitPrice;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }
}
