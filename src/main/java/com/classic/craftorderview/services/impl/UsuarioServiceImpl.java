package com.classic.craftorderview.services.impl;

import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.request.UsuarioRequestDto;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDto;
import com.classic.craftorderview.services.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final WebClient webClient;

    public UsuarioServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<UsuarioResponseDto> listar() {
        return webClient.get()
                .uri("/usuarios")
                .retrieve()
                .bodyToFlux(UsuarioResponseDto.class)
                .collectList()
                .block();
    }

    @Override
    public void crear(UsuarioRequestDto dto) {
        webClient.post()
                .uri("/usuarios")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void desactivar(Long id) {
        webClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public UsuarioResponseDto autenticar(LoginRequestDTO dto) {
        return webClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UsuarioResponseDto.class)
                .block();
    }
}
