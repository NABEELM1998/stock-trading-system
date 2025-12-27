package com.nabeel.market_data_service.scheduler;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketPriceStore {

    public static final Map<String, MarketPricesResponse> PRICES =
            new ConcurrentHashMap<>();

    public static Map<String, MarketPricesResponse> getPrices() {
        return PRICES;
    }
}
