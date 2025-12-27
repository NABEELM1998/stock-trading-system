package com.nabeel.order_service.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPricesResponse {

    private String symbol;
    private BigDecimal askPrice;
    private BigDecimal bidPrice;
    private BigDecimal lastExecutedPrice;
}
