package com.nabeel.market_data_service.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPricesResponse {
    @Schema(example = "AAPL", description = "Stock symbol")
    private String symbol;
    @Schema(example = "100.00", description = "ask price")
    private BigDecimal askPrice;
    @Schema(example = "100.88", description = "bid price")
    private BigDecimal bidPrice;
    @Schema(example = "900", description = "Last executed price")
    private BigDecimal lastExecutedPrice;
}
