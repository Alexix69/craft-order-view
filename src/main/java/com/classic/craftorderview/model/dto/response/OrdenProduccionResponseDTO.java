package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class OrdenProduccionResponseDTO {
    private Long id;
    private Long cotizacionId;
    private Long artesanoId;
    private String artesanoNombre;
    private String estadoActual;
    private String tipoMuebleNombre;
    private String materialNombre;
    private String tipoAcabado;
    private BigDecimal altoCm;
    private BigDecimal anchoCm;
    private BigDecimal profundidadCm;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private BigDecimal costoAprobado;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinalizacion;
    private OffsetDateTime createdAt;
    private String pdfUrl;
}
