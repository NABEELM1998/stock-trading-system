package com.nabeel.market_data_service.service;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;
import com.nabeel.market_data_service.Responses.MarketStatusResponse;
import com.nabeel.market_data_service.scheduler.MarketPriceStore;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MarketDateServiceImpl implements MarketDataService{
    @Override
    public MarketStatusResponse getMarketStatus() {
        ZoneId EST_ZONE = ZoneId.of("America/New_York");
         LocalTime START_TIME = LocalTime.of(9, 30);
         LocalTime END_TIME = LocalTime.of(16, 0);
        ZonedDateTime nowInEst = ZonedDateTime.now(EST_ZONE);
        LocalTime currentTime = nowInEst.toLocalTime();

        return new MarketStatusResponse(!currentTime.isBefore(START_TIME)
                 && !currentTime.isAfter(END_TIME));
    }

    @Override
    public MarketPricesResponse getMarketPrice(String symbol) {
        return MarketPriceStore.getPrices().get(symbol);
    }

    @PostConstruct
    public void loadPrices() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is =
                     new ClassPathResource("market-prices.json").getInputStream()) {

            List<MarketPricesResponse> responses =  objectMapper.readValue(
                    is,
                    new TypeReference<List<MarketPricesResponse>>() {}
            );
            responses.forEach(response->{
                MarketPriceStore.PRICES.put(response.getSymbol(),response);
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to load market prices", e);
        }
    }
}
