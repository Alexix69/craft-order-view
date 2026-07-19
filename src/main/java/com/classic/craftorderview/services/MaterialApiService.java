package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.AcabadoPorcentajeRequestDTO;
import com.classic.craftorderview.model.dto.request.MaterialRequestDTO;
import com.classic.craftorderview.model.dto.response.AcabadoResponseDTO;
import com.classic.craftorderview.model.dto.response.MaterialResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class MaterialApiService {

    private final WebClient webClient;

    public MaterialApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<MaterialResponseDTO> listar() {
        return webClient.get()
                .uri("/materiales")
                .retrieve()
                .bodyToFlux(MaterialResponseDTO.class)
                .collectList()
                .block();
    }

    public void crear(MaterialRequestDTO dto) {
        webClient.post()
                .uri("/materiales")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void desactivar(Long id) {
        webClient.delete()
                .uri("/materiales/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<AcabadoResponseDTO> listarAcabados() {
        return webClient.get()
                .uri("/materiales/acabados")
                .retrieve()
                .bodyToFlux(AcabadoResponseDTO.class)
                .collectList()
                .block();
    }

    public void actualizarPorcentajeAcabado(String tipo,
                                             AcabadoPorcentajeRequestDTO dto) {
        webClient.post()
                .uri("/materiales/acabados/{tipo}/porcentaje", tipo)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
