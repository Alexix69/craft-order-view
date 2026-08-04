package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TipoMuebleResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String fotoUrl;
    private BigDecimal costoBaseMo;
    private Boolean activo;
}
