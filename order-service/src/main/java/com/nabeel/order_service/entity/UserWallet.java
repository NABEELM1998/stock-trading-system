package com.nabeel.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

