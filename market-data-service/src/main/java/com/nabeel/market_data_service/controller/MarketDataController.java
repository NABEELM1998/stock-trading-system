package com.nabeel.market_data_service.controller;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;
import com.nabeel.market_data_service.Responses.MarketStatusResponse;
import com.nabeel.market_data_service.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
@Tag(name = "Market Prices", description = "Market price APIs")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }
    @GetMapping("/status")
    @Operation(summary = "Get market status",
            description = "Returns whether market is open or not")
    public ResponseEntity<MarketStatusResponse> getMarketStatus(){
        return ResponseEntity.ok(marketDataService.getMarketStatus());
    }

    @GetMapping("/price/{symbol}")
    @Operation(summary = "Get price by symbol",
            description = "Returns market price for a given stock symbol")
    public ResponseEntity<MarketPricesResponse> getMarketPrices(
            @PathVariable String symbol) {

        return ResponseEntity.ok(
                marketDataService.getMarketPrice(symbol)
        );
    }

}
