package com.nabeel.order_service.dto;

public class OrderValidationRequest {
    private String symbol;
    private Integer quantity;
    private String orderType;
    private Double limitPrice;

    public OrderValidationRequest() {
    }

    public OrderValidationRequest(String symbol, Integer quantity, String orderType, Double limitPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.orderType = orderType;
        this.limitPrice = limitPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
