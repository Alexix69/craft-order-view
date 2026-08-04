package com.classic.craftorderview.model.dto.request;

import lombok.Data;

@Data
public class UsuarioRequestDto {
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;
}
