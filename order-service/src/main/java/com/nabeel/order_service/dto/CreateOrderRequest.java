package com.nabeel.order_service.dto;

import com.nabeel.order_service.entity.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Side is required")
    private Order.OrderSide side;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Order type is required")
    private Order.OrderType orderType;

    private Long userId;

    private BigDecimal limitPrice; // Required for LIMIT orders, null for MARKET orders
}

