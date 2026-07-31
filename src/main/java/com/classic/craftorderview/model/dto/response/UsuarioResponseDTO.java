package com.classic.craftorderview.model.dto.response;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private Boolean activo;
    private Boolean primerLogin;
}
