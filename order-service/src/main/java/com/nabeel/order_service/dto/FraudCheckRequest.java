package com.nabeel.order_service.dto;

public class FraudCheckRequest {
    private Long userId;
    private String symbol;
    private Integer quantity;
    private Double amount;

    public FraudCheckRequest() {
    }

    public FraudCheckRequest(Long userId, String symbol, Integer quantity, Double amount) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.amount = amount;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
