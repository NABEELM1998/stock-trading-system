package com.nabeel.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionResult {
    private boolean executed;
    private BigDecimal executionPrice;
    private Integer filledQuantity;
    private BigDecimal fees;
    private String message;
}
