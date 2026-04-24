package com.icaro.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private String sessionId;
    private List<CartItemResponseDTO> items;
    private BigDecimal total;
}

