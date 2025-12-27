package com.nabeel.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderSide side;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderType orderType;

    @Column(precision = 19, scale = 4)
    private BigDecimal limitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(length = 100)
    private String workflowId;

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderType {
        MARKET, LIMIT
    }

    public enum OrderStatus {
        PENDING,
        VALIDATING,
        FRAUD_CHECK,
        EXECUTING,
        FILLED,
        REJECTED,
        FAILED
    }
}

