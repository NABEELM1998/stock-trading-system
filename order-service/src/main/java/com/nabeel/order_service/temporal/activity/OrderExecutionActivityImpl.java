package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.ExecutionResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.entity.Order;
import com.nabeel.order_service.exceptions.OrderExecutionException;
import com.nabeel.order_service.responses.MarketPricesResponse;
import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class OrderExecutionActivityImpl implements OrderExecutionActivity {
    private static final Logger logger = LoggerFactory.getLogger(OrderExecutionActivityImpl.class);
    @Autowired
    private RestTemplate restTemplate;
    @Value("${market.service.base-url}")
    private String marketServiceBaseUrl;

    @Override
    public ExecutionResult executeOrder(WorkflowRequest request) throws OrderExecutionException{
        logger.info("Executing order");
        Activity.getExecutionContext().heartbeat("Executing order");

            MarketPricesResponse marketPricesResponse = restTemplate.getForObject(marketServiceBaseUrl+"/api/v1/market/price/"+request.getSymbol(), MarketPricesResponse.class);
            BigDecimal marketPrice = marketPricesResponse.getLastExecutedPrice();
            BigDecimal executionPrice = null;
            Integer filledQuantity = 0;
            boolean isOrderExecutable = true;
            String message = "";
            if(Objects.equals(request.getSide(), Order.OrderSide.BUY.name())){
                BigDecimal askPrice = marketPricesResponse.getAskPrice();
                if(Objects.equals(request.getOrderType(), Order.OrderType.MARKET.name())){
                    executionPrice = marketPricesResponse.getLastExecutedPrice();
                    filledQuantity = request.getQuantity();
                }else {
                    if(Objects.equals(request.getLimitPrice(), askPrice)){
                        executionPrice = askPrice;
                        filledQuantity = request.getQuantity();

                    }else{
                        isOrderExecutable = false;
                        message = "Prices not matched";
                    }
                }

            } else {
                BigDecimal bidPrice = marketPricesResponse.getBidPrice();
                if(Objects.equals(request.getOrderType(), Order.OrderType.MARKET.name())){
                    executionPrice = marketPricesResponse.getLastExecutedPrice();
                    filledQuantity = request.getQuantity();
                }else {

                    if(Objects.equals(request.getLimitPrice(),bidPrice)){
                        executionPrice = bidPrice;
                        filledQuantity = request.getQuantity();
                    }else {
                        isOrderExecutable = false;
                        message = "Prices not matched";
                    }

                }
            }
            if(!isOrderExecutable){
                logger.error("Order Execution failed due to {}",message);
                throw new OrderExecutionException(message);
            }

            BigDecimal fees = executionPrice != null ? executionPrice.multiply(BigDecimal.valueOf(filledQuantity* 0.001))  : BigDecimal.ZERO;
            logger.info("Order executed: executionPrice={}, filledQuantity={}, fees={}", 
                    executionPrice, filledQuantity, fees);
            request.setExecutionPrice(executionPrice);
            request.setFees(fees);
            return new ExecutionResult(true, executionPrice, filledQuantity, fees, 
                String.format("Order executed at %.2f", executionPrice));

    }
}

