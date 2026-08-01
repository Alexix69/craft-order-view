package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.AcabadoPorcentajeRequestDTO;
import com.classic.craftorderview.model.dto.request.MaterialRequestDTO;
import com.classic.craftorderview.model.dto.response.AcabadoResponseDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import com.classic.craftorderview.model.dto.response.MaterialResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class MaterialApiService {

    private final WebClient webClient;

    public MaterialApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public PaginaResponseDTO<MaterialResponseDTO> listarTodosPaginado(String nombre, int page) {
        try {
            return webClient.get()
                    .uri(u -> u.path("/materiales")
                            .queryParam("nombre", nombre != null ? nombre : "")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<MaterialResponseDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public PaginaResponseDTO<MaterialResponseDTO> listarActivosPaginado(String nombre, int page) {
        try {
            return webClient.get()
                    .uri(u -> u.path("/materiales/activos")
                            .queryParam("nombre", nombre != null ? nombre : "")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<MaterialResponseDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public MaterialResponseDTO buscarPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/materiales/{id}", id)
                    .retrieve()
                    .bodyToMono(MaterialResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void crear(MaterialRequestDTO dto) {
        try {
            webClient.post()
                    .uri("/materiales")
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void actualizar(Long id, MaterialRequestDTO dto) {
        try {
            webClient.put()
                    .uri("/materiales/{id}", id)
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void activar(Long id) {
        try {
            webClient.post()
                    .uri("/materiales/{id}/activar", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void desactivar(Long id) {
        try {
            webClient.post()
                    .uri("/materiales/{id}/desactivar", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public List<AcabadoResponseDTO> listarAcabados() {
        try {
            return webClient.get()
                    .uri("/tipos-acabado")
                    .retrieve()
                    .bodyToFlux(AcabadoResponseDTO.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void actualizarPorcentajeAcabado(String tipo, AcabadoPorcentajeRequestDTO dto) {
        try {
            webClient.post()
                    .uri("/tipos-acabado/{tipo}/porcentaje", tipo)
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    private String extraerMensaje(WebClientResponseException e) {
        ErrorResponseDTO error = e.getResponseBodyAs(ErrorResponseDTO.class);
        return error != null ? error.getMensaje() : "Error de comunicación con el servidor";
    }
}
