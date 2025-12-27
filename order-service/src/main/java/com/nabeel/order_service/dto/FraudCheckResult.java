package com.nabeel.order_service.dto;

public class FraudCheckResult {
    private boolean passed;
    private String reason;

    public FraudCheckResult() {
    }

    public FraudCheckResult(boolean passed, String reason) {
        this.passed = passed;
        this.reason = reason;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
