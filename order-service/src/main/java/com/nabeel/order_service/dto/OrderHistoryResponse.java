package com.nabeel.order_service.dto;

import com.nabeel.order_service.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Long id;
    private Long orderId;
    private Order.OrderStatus status;
    private String reason;
    private LocalDateTime changedAt;
}

