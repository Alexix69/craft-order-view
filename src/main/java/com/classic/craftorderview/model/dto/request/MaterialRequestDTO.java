package com.classic.craftorderview.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialRequestDTO {
    private String nombre;
    private BigDecimal precioPorM3;
}
