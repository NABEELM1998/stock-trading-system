package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.SettlementRequest;
import com.nabeel.order_service.dto.SettlementResult;
import com.nabeel.order_service.entity.UserWallet;
import com.nabeel.order_service.entity.UserWalletHistory;
import com.nabeel.order_service.repository.UserWalletHistoryRepository;
import com.nabeel.order_service.repository.UserWalletRepository;
import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class SettlementActivityImpl implements SettlementActivity {
    private static final Logger logger = LoggerFactory.getLogger(SettlementActivityImpl.class);

    @Autowired
    private UserWalletRepository walletRepository;

    @Autowired
    private UserWalletHistoryRepository walletHistoryRepository;

    @Override
    @Transactional
    public SettlementResult settleOrder(CreateOrderRequest request) {
        logger.info("Settling order: orderId={}, userId={}, side={}, quantity={}, executionPrice={}, fees={}", 
                request.getOrderId(), request.getUserId(), request.getSide(), 
                request.getQuantity(), request.getExecutionPrice(), request.getFees());

        Activity.getExecutionContext().heartbeat("Settling order");

        try {
            UserWallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + request.getUserId()));

            BigDecimal totalAmount = BigDecimal.valueOf(request.getExecutionPrice())
                    .multiply(BigDecimal.valueOf(request.getQuantity()))
                    .add(BigDecimal.valueOf(request.getFees()));

            BigDecimal balanceBefore = wallet.getBalance();
            BigDecimal balanceAfter;

            if ("BUY".equals(request.getSide())) {
                // Debit for BUY orders
                if (wallet.getBalance().compareTo(totalAmount) < 0) {
                    return new SettlementResult(false, "Insufficient balance");
                }
                balanceAfter = balanceBefore.subtract(totalAmount);
            } else {
                // Credit for SELL orders
                balanceAfter = balanceBefore.add(totalAmount.subtract(BigDecimal.valueOf(request.getFees())));
            }

            wallet.setBalance(balanceAfter);
            walletRepository.save(wallet);

            // Record transaction history
            UserWalletHistory.TransactionType transactionType = "BUY".equals(request.getSide()) 
                    ? UserWalletHistory.TransactionType.ORDER_DEBIT 
                    : UserWalletHistory.TransactionType.ORDER_CREDIT;

            UserWalletHistory history = UserWalletHistory.builder()
                    .userId(request.getUserId())
                    .transactionType(transactionType)
                    .amount(totalAmount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .orderId(request.getOrderId())
                    .description(String.format("Order %s settlement: %d shares at %.2f", 
                            request.getSide(), request.getQuantity(), request.getExecutionPrice()))
                    .build();

            walletHistoryRepository.save(history);

            logger.info("Settlement completed: balanceBefore={}, balanceAfter={}", balanceBefore, balanceAfter);
            return new SettlementResult(true, "Settlement completed successfully");
        } catch (Exception e) {
            logger.error("Error settling order", e);
            return new SettlementResult(false, "Settlement failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void compensateSettlement(SettlementRequest request) {
        logger.info("Compensating settlement for orderId={}, userId={}", request.getOrderId(), request.getUserId());

        try {
            UserWallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + request.getUserId()));

            BigDecimal totalAmount = BigDecimal.valueOf(request.getExecutionPrice())
                    .multiply(BigDecimal.valueOf(request.getQuantity()))
                    .add(BigDecimal.valueOf(request.getFees()));

            BigDecimal balanceBefore = wallet.getBalance();
            BigDecimal balanceAfter;

            // Reverse the transaction
            if ("BUY".equals(request.getSide())) {
                // Credit back for BUY orders
                balanceAfter = balanceBefore.add(totalAmount);
            } else {
                // Debit back for SELL orders
                balanceAfter = balanceBefore.subtract(totalAmount.subtract(BigDecimal.valueOf(request.getFees())));
            }

            wallet.setBalance(balanceAfter);
            walletRepository.save(wallet);

            // Record refund transaction
            UserWalletHistory history = UserWalletHistory.builder()
                    .userId(request.getUserId())
                    .transactionType(UserWalletHistory.TransactionType.REFUND)
                    .amount(totalAmount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .orderId(request.getOrderId())
                    .description(String.format("Refund for order %d", request.getOrderId()))
                    .build();

            walletHistoryRepository.save(history);

            logger.info("Settlement compensation completed");
        } catch (Exception e) {
            logger.error("Error compensating settlement", e);
            throw new RuntimeException("Failed to compensate settlement", e);
        }
    }
}

