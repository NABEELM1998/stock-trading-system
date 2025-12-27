package com.nabeel.market_data_service.scheduler;

import com.nabeel.market_data_service.Responses.MarketPricesResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
public class MarketPriceUpdater implements Runnable{


    @Override
    public void run() {
        log.info("scheduler running");

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
