package com.nabeel.order_service.repository;

import com.nabeel.order_service.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);
}

