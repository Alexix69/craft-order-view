package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UsuarioResponseDto {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private OffsetDateTime createdAt;
}
