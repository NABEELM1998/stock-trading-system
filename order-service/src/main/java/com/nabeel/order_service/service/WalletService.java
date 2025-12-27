package com.nabeel.order_service.service;

import com.nabeel.order_service.entity.UserWallet;
import com.nabeel.order_service.entity.UserWalletHistory;
import com.nabeel.order_service.repository.UserWalletHistoryRepository;
import com.nabeel.order_service.repository.UserWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {

    @Autowired
    private UserWalletRepository walletRepository;

    @Autowired
    private UserWalletHistoryRepository walletHistoryRepository;

    @Transactional
    public UserWallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserWallet wallet = UserWallet.builder()
                            .userId(userId)
                            .balance(BigDecimal.ZERO)
                            .currency("USD")
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return walletRepository.save(wallet);
                });
    }

    public UserWallet getWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    @Transactional
    public void deposit(Long userId, BigDecimal amount, String description) {
        UserWallet wallet = getOrCreateWallet(userId);
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        wallet.setBalance(balanceAfter);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Record history
        UserWalletHistory history = UserWalletHistory.builder()
                .userId(userId)
                .transactionType(UserWalletHistory.TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
        walletHistoryRepository.save(history);
    }
}

