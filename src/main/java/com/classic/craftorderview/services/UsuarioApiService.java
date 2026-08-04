package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
}
