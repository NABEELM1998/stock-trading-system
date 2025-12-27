package com.nabeel.order_service.dto;

public class SettlementResult {
    private boolean settled;
    private String message;

    public SettlementResult() {
    }

    public SettlementResult(boolean settled, String message) {
        this.settled = settled;
        this.message = message;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
