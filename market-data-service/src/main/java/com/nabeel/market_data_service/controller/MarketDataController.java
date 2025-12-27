package com.nabeel.market_data_service.controller;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;
import com.nabeel.market_data_service.Responses.MarketStatusResponse;
import com.nabeel.market_data_service.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }
    @GetMapping("/status")
    public ResponseEntity<MarketStatusResponse> getMarketStatus(){
        return ResponseEntity.ok(marketDataService.getMarketStatus());
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<MarketPricesResponse> getMarketPrices(
            @PathVariable String symbol) {

        return ResponseEntity.ok(
                marketDataService.getMarketPrice(symbol)
        );
    }

}
