package com.classic.craftorderview.model.dto.response;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String mensaje;
    private String codigo;
    private String campo;
}
