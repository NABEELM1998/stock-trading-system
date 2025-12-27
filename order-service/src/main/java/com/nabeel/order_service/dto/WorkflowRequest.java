package com.nabeel.order_service.dto;

import com.nabeel.order_service.entity.Order;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Data
@Setter
public class WorkflowRequest {
    private Long orderId;
    private Long userId;
    private String symbol;
    private String side;
    private Integer quantity;
    private String orderType;
    private BigDecimal limitPrice;
    private Order order;
    private BigDecimal executionPrice;
    private BigDecimal fees;

    public WorkflowRequest() {
    }

    public WorkflowRequest(Long orderId, Long userId, String symbol, String side,
                           Integer quantity, String orderType, BigDecimal limitPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.orderType = orderType;
        this.limitPrice = limitPrice;
    }

}
