package com.nabeel.order_service.service;

import com.nabeel.order_service.dto.*;
import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.entity.OrderHistory;
import com.nabeel.order_service.repository.OrderHistoryRepository;
import com.nabeel.order_service.repository.OrderRepository;
import com.nabeel.order_service.security.UserPrincipal;
import com.nabeel.order_service.temporal.workflow.TradeOrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private WorkflowClient workflowClient;
    @Autowired
    private WorkflowServiceStubs serviceStubs;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long userId = UserPrincipal.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
            request.setUserId(userId);
        // Validate limit price for LIMIT orders
        if (request.getOrderType() == Order.OrderType.LIMIT && request.getLimitPrice() == null) {
            throw new IllegalArgumentException("Limit price is required for LIMIT orders");
        }

        // Create order entity
        Order order = Order.builder()
                .userId(userId)
                .symbol(request.getSymbol().toUpperCase())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .orderType(request.getOrderType())
                .limitPrice(request.getLimitPrice())
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        // Record initial status in history
        recordOrderHistory(order.getOrderId(), Order.OrderStatus.PENDING, "Order created");

        // Generate workflow ID
        String workflowId = "order-workflow-" + order.getOrderId() + "-" + UUID.randomUUID().toString();
        order.setWorkflowId(workflowId);
        order = orderRepository.save(order);

        // Start Temporal workflow
        try {
            TradeOrderWorkflow workflow = workflowClient.newWorkflowStub(
                    TradeOrderWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId(workflowId)
                            .setTaskQueue("order-processing")
                            .setWorkflowExecutionTimeout(java.time.Duration.ofMinutes(5))
                            .build());

            // Execute workflow asynchronously
            WorkflowRequest workflowRequest = new WorkflowRequest(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getSymbol(),
                    order.getSide().name(),
                    order.getQuantity(),
                    order.getOrderType().name(),
                    order.getLimitPrice() != null ? order.getLimitPrice().doubleValue() : null
            );

            // Start workflow execution asynchronously
            WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
            untyped.start(workflowRequest);

            logger.info("Workflow started for orderId={}, workflowId={}", order.getOrderId(), workflowId);
        } catch (Exception e) {
            logger.error("Failed to start workflow for orderId={}", order.getOrderId(), e);
            order.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(order);
            recordOrderHistory(order.getOrderId(), Order.OrderStatus.FAILED, "Failed to start workflow: " + e.getMessage());
        }

        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderById(Long orderId) {
        Long userId = UserPrincipal.getCurrentUserId();
        boolean isAdmin = UserPrincipal.isAdmin();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Check access: users can only see their own orders unless they're admin
        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Try to get workflow status if workflow is running
        if (order.getWorkflowId() != null) {
            try {
                TradeOrderWorkflow workflow = workflowClient.newWorkflowStub(
                        TradeOrderWorkflow.class, order.getWorkflowId());
                String workflowStatus = workflow.getStatus();
                // Update order status if workflow status differs
                if (workflowStatus != null && !workflowStatus.equals(order.getStatus().name())) {
                    try {
                        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(workflowStatus);
                        if (newStatus != order.getStatus()) {
                            updateOrderStatus(orderId, newStatus, "Status updated from workflow");
                        }
                        order = orderRepository.findById(orderId).orElse(order);
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid status values
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not query workflow status", e);
            }
        }

        return mapToOrderResponse(order);
    }

    public PagedResponse<OrderResponse> getOrders(String status, String symbol, int page, int size) {
        Long userId = UserPrincipal.getCurrentUserId();
        boolean isAdmin = UserPrincipal.isAdmin();

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage;

        if (isAdmin) {
            // Admin can see all orders
            if (status != null && symbol != null) {
                orderPage = orderRepository.findAll(pageable);
                // Filter manually for admin (you might want to add admin-specific queries)
                orderPage = orderPage.filter(o -> 
                    (status == null || o.getStatus().name().equals(status)) &&
                    (symbol == null || o.getSymbol().equals(symbol))
                );
            } else if (status != null) {
                orderPage = orderRepository.findAll(pageable);
                orderPage = orderPage.filter(o -> o.getStatus().name().equals(status));
            } else if (symbol != null) {
                orderPage = orderRepository.findAll(pageable);
                orderPage = orderPage.filter(o -> o.getSymbol().equals(symbol));
            } else {
                orderPage = orderRepository.findAll(pageable);
            }
        } else {
            // Regular users can only see their own orders
            if (status != null && symbol != null) {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orderPage = orderRepository.findByUserIdAndStatusAndSymbol(userId, orderStatus, symbol, pageable);
            } else if (status != null) {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orderPage = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
            } else if (symbol != null) {
                orderPage = orderRepository.findByUserIdAndSymbol(userId, symbol, pageable);
            } else {
                orderPage = orderRepository.findByUserId(userId, pageable);
            }
        }

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return PagedResponse.<OrderResponse>builder()
                .content(content)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    public List<OrderHistoryResponse> getOrderHistory(Long orderId) {
        Long userId = UserPrincipal.getCurrentUserId();
        boolean isAdmin = UserPrincipal.isAdmin();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Check access
        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        List<OrderHistory> history = orderHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);
        return history.stream()
                .map(h -> OrderHistoryResponse.builder()
                        .id(h.getId())
                        .orderId(h.getOrderId())
                        .status(h.getStatus())
                        .reason(h.getReason())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        recordOrderHistory(orderId, status, reason != null ? reason : 
                String.format("Status changed from %s to %s", oldStatus, status));
    }

    private void recordOrderHistory(Long orderId, Order.OrderStatus status, String reason) {
        OrderHistory history = OrderHistory.builder()
                .orderId(orderId)
                .status(status)
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .build();
        orderHistoryRepository.save(history);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .symbol(order.getSymbol())
                .side(order.getSide())
                .quantity(order.getQuantity())
                .orderType(order.getOrderType())
                .limitPrice(order.getLimitPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .workflowId(order.getWorkflowId())
                .build();
    }
}

