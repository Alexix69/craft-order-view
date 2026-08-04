package com.classic.craftorderview.model.dto.request;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
}
