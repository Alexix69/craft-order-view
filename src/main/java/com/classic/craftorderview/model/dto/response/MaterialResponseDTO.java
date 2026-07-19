package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialResponseDTO {
    private Long id;
    private String nombre;
    private BigDecimal precioPorM3;
    private Boolean activo;
}
