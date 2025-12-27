package com.nabeel.order_service.dto;

import com.nabeel.order_service.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String symbol;
    private Order.OrderSide side;
    private Integer quantity;
    private Order.OrderType orderType;
    private BigDecimal limitPrice;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String workflowId;
}

