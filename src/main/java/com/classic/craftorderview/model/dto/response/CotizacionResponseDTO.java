package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CotizacionResponseDTO {
    private Long id;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private Long tipoMuebleId;
    private Long materialId;
    private String tipoAcabado;
    private BigDecimal altoCm;
    private BigDecimal anchoCm;
    private BigDecimal profundidadCm;
    private BigDecimal precioMaterialSnap;
    private BigDecimal costoBaseMoSnap;
    private BigDecimal porcentajeAcabadoSnap;
    private BigDecimal costoEstimado;
    private BigDecimal costoAprobado;
    private String estado;
    private String token;
}
