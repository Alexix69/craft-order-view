package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AcabadoResponseDTO {
    private Long id;
    private String tipo;
    private BigDecimal porcentaje;
}
