package com.classic.craftorderview.model.dto.request;

import lombok.Data;

@Data
public class CambioEstadoRequestDTO {
    private String estadoNuevo;
    private String motivo;
}
