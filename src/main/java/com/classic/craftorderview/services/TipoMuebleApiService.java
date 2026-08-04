package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.TipoMuebleRequestDTO;
import com.classic.craftorderview.model.dto.response.TipoMuebleResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TipoMuebleApiService {

    private final WebClient webClient;

    public TipoMuebleApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<TipoMuebleResponseDTO> listar() {
        return webClient.get()
                .uri("/tipos-mueble")
                .retrieve()
                .bodyToFlux(TipoMuebleResponseDTO.class)
                .collectList()
                .block();
    }

    public void crear(TipoMuebleRequestDTO dto) {
        webClient.post()
                .uri("/tipos-mueble")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void desactivar(Long id) {
        webClient.delete()
                .uri("/tipos-mueble/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
