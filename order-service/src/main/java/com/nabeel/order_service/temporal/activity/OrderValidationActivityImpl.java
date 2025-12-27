package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.MarketStatusResponse;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.exceptions.ValidationException;
import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class OrderValidationActivityImpl implements OrderValidationActivity {
    private static final Logger logger = LoggerFactory.getLogger(OrderValidationActivityImpl.class);
    
    private static final Set<String> VALID_SYMBOLS = Set.of("AAPL", "TSLA", "GOOGL", "MSFT", "AMZN", "META", "NVDA");
    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 30);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(16, 0);

    @Autowired
    private RestTemplate restTemplate;
    @Value("${market.service.base-url}")
    private String marketServiceBaseUrl;

    @Override
    public void validateOrder(WorkflowRequest request) throws ValidationException {
        logger.info("Validating order: symbol={}, quantity={}, orderType={}, limitPrice={}", 
                request.getSymbol(), request.getQuantity(), request.getOrderType(), request.getLimitPrice());

        // Record heartbeat for long-running activities
        Activity.getExecutionContext().heartbeat("Validating order parameters");

        // Validate quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be positive");
        }

        // Validate order type
        if (request.getOrderType() == null || (!request.getOrderType().equals(Order.OrderType.MARKET.name()) && !request.getOrderType().equals(Order.OrderType.LIMIT.name()))) {
            throw new ValidationException("Invalid order type: " + request.getOrderType());
        }

        // Validate limit price for LIMIT orders
        if (Order.OrderType.LIMIT.name().equals(request.getOrderType())
                && (request.getLimitPrice() == null
                || request.getLimitPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new ValidationException("Limit price is required and must be positive for LIMIT orders");
        }

        // Check market hours
        LocalTime now = LocalTime.now();
        if (now.isBefore(MARKET_OPEN) || now.isAfter(MARKET_CLOSE)) {
            throw new ValidationException(
                String.format("Market is closed. Trading hours: %s - %s", MARKET_OPEN, MARKET_CLOSE));
        }

        // Check market status via service
        String marketStatusUrl = marketServiceBaseUrl + "/api/v1/market/status";
        boolean isMarketOpen;
        try {
            MarketStatusResponse response =
                    restTemplate.getForObject(marketStatusUrl, MarketStatusResponse.class);
            isMarketOpen = (response != null && Boolean.TRUE.equals(response.isOpen()));
        } catch (Exception e) {
            logger.error("Could not reach market status service: {}", e.getMessage());
            throw new ValidationException("Could not verify market status");
        }
        if (!Boolean.TRUE.equals(isMarketOpen)) {
            throw new ValidationException("Market is currently closed according to the service");
        }

        logger.info("Order validation successful");
    }
}

