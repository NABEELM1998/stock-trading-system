package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.repository.OrderRepository;
import com.nabeel.order_service.repository.OrderHistoryRepository;
import com.nabeel.order_service.entity.OrderHistory;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@ActivityInterface
public interface OrderStatusUpdateActivity {
    @ActivityMethod
    void updateOrderStatus(Long orderId, String status, String reason);
}

