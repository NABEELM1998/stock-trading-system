package com.nabeel.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_wallet_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Column
    private Long orderId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        ORDER_DEBIT,
        ORDER_CREDIT,
        REFUND
    }
}

