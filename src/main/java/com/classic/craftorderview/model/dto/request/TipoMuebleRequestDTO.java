package com.classic.craftorderview.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TipoMuebleRequestDTO {
    private String nombre;
    private String descripcion;
    private String fotoUrl;
    private BigDecimal costoBaseMo;
}
