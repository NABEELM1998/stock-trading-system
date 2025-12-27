package com.nabeel.market_data_service.service;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;
import com.nabeel.market_data_service.Responses.MarketStatusResponse;

public interface MarketDataService {

    public MarketStatusResponse getMarketStatus();
    public MarketPricesResponse getMarketPrice(String symbol);
}
