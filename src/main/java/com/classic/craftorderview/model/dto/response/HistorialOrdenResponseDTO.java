package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HistorialOrdenResponseDTO {
    private String tipoEvento;
    private String valorAnterior;
    private String valorNuevo;
    private String motivo;
    private OffsetDateTime createdAt;
}
