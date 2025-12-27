package com.nabeel.order_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MarketStatusResponse {
    private boolean isOpen;
}
