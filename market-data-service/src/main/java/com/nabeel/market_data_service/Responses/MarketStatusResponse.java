package com.nabeel.market_data_service.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MarketStatusResponse {
    private boolean isOpen;
}
