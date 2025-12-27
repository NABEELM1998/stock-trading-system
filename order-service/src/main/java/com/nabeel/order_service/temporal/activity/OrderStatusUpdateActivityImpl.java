package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.entity.OrderHistory;
import com.nabeel.order_service.repository.OrderHistoryRepository;
import com.nabeel.order_service.repository.OrderRepository;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class OrderStatusUpdateActivityImpl implements OrderStatusUpdateActivity {
    private static final Logger logger = LoggerFactory.getLogger(OrderStatusUpdateActivityImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status, String reason) {
        logger.info("Updating order status: orderId={}, status={}, reason={}", orderId, status, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
            Order.OrderStatus oldStatus = order.getStatus();

            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // Record history
            OrderHistory history = OrderHistory.builder()
                    .orderId(orderId)
                    .status(newStatus)
                    .reason(reason != null ? reason : String.format("Status changed from %s to %s", oldStatus, newStatus))
                    .changedAt(LocalDateTime.now())
                    .build();
            orderHistoryRepository.save(history);

            logger.info("Order status updated successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {}", status, e);
            throw new RuntimeException("Invalid order status: " + status, e);
        }
    }
}
