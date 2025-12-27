package com.nabeel.market_data_service.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MarketStatusResponse {
    @Schema(example = "true", description = "Market is open or not")
    private boolean isOpen;
}
