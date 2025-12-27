package com.nabeel.order_service.controller;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.OrderHistoryResponse;
import com.nabeel.order_service.dto.OrderResponse;
import com.nabeel.order_service.dto.PagedResponse;
import com.nabeel.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        try {
            OrderResponse response = orderService.getOrderById(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PagedResponse<OrderResponse> response = orderService.getOrders(status, symbol, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable Long orderId) {
        try {
            List<OrderHistoryResponse> response = orderService.getOrderHistory(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

