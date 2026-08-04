package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.request.UsuarioRequestDto;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDto;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioResponseDto> listar();

    void crear(UsuarioRequestDto dto);

    void desactivar(Long id);

    // Soporte al login existente (HU de autenticación)
    UsuarioResponseDto autenticar(LoginRequestDTO dto);
}
