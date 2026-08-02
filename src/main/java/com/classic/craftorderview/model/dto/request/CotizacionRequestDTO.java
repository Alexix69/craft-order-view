package com.classic.craftorderview.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CotizacionRequestDTO {
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private Long tipoMuebleId;
    private Long materialId;
    private String tipoAcabado;
    private BigDecimal altoCm;
    private BigDecimal anchoCm;
    private BigDecimal profundidadCm;
}
