package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class FacturaResponseDTO {
    private Long id;
    private String numeroFactura;
    private String descripcionMueble;
    private BigDecimal montoTotal;
    private String pdfUrl;
    private OffsetDateTime createdAt;
}
