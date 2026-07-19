package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.request.UsuarioRequestDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UsuarioApiService {

    private final WebClient webClient;

    public UsuarioApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public UsuarioResponseDTO autenticar(LoginRequestDTO dto) {
        return webClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .block();
    }

    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usuarios")
                        .queryParam("rol", rol)
                        .build())
                .retrieve()
                .bodyToFlux(UsuarioResponseDTO.class)
                .collectList()
                .block();
    }

    public void crear(UsuarioRequestDTO dto) {
        webClient.post()
                .uri("/usuarios")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void desactivar(Long id) {
        webClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
