package com.icaro.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço precisa ser maior que zero!")
    private BigDecimal price;

    @NotNull(message = "Estoque é obrigatório")
    @Min(value = 0, message = "O estoque não pode ser valor negativo!")
    private Integer stock;

    private String imageUrl;
}
