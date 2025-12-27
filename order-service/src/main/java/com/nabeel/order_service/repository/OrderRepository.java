package com.nabeel.order_service.repository;

import com.nabeel.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);
    Page<Order> findByUserIdAndSymbol(Long userId, String symbol, Pageable pageable);
    Page<Order> findByUserIdAndStatusAndSymbol(Long userId, Order.OrderStatus status, String symbol, Pageable pageable);
    Optional<Order> findByWorkflowId(String workflowId);
    List<Order> findByUserId(Long userId);
}

