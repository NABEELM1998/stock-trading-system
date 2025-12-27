package com.nabeel.order_service.dto;

public class ExecutionRequest {
    private Long orderId;
    private String symbol;
    private String side;
    private Integer quantity;
    private String orderType;
    private Double limitPrice;

    public ExecutionRequest() {
    }

    public ExecutionRequest(Long orderId, String symbol, String side, Integer quantity,
                            String orderType, Double limitPrice) {
        this.orderId = orderId;
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
