package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.AsignacionRequestDTO;
import com.classic.craftorderview.model.dto.request.CambioEstadoRequestDTO;
import com.classic.craftorderview.model.dto.request.ReasignacionRequestDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import com.classic.craftorderview.model.dto.response.HistorialOrdenResponseDTO;
import com.classic.craftorderview.model.dto.response.OrdenProduccionResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class OrdenApiService {

    private final WebClient webClient;

    public OrdenApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<OrdenProduccionResponseDTO> listarActivas() {
        try {
            return webClient.get()
                    .uri("/ordenes")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<OrdenProduccionResponseDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al listar las órdenes"));
        }
    }

    public List<OrdenProduccionResponseDTO> listarPorArtesano(Long artesanoId) {
        try {
            return webClient.get()
                    .uri("/ordenes/artesano/{artesanoId}", artesanoId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<OrdenProduccionResponseDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al listar las órdenes del artesano"));
        }
    }

    public OrdenProduccionResponseDTO buscarPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/ordenes/{id}", id)
                    .retrieve()
                    .bodyToMono(OrdenProduccionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Orden no encontrada"));
        }
    }

    public OrdenProduccionResponseDTO asignar(Long cotizacionId, Long artesanoId,
                                               Long realizadoPor) {
        try {
            AsignacionRequestDTO dto = new AsignacionRequestDTO();
            dto.setArtesanoId(artesanoId);
            return webClient.post()
                    .uri(u -> u.path("/ordenes/asignar")
                            .queryParam("cotizacionId", cotizacionId)
                            .queryParam("realizadoPor", realizadoPor)
                            .build())
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(OrdenProduccionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al asignar el artesano"));
        }
    }

    public OrdenProduccionResponseDTO cambiarEstado(Long ordenId, String estadoNuevo,
                                                      String motivo, Long realizadoPor) {
        try {
            CambioEstadoRequestDTO dto = new CambioEstadoRequestDTO();
            dto.setEstadoNuevo(estadoNuevo);
            dto.setMotivo(motivo);
            return webClient.post()
                    .uri(u -> u.path("/ordenes/{id}/estado")
                            .queryParam("realizadoPor", realizadoPor)
                            .build(ordenId))
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(OrdenProduccionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al cambiar el estado de la orden"));
        }
    }

    public OrdenProduccionResponseDTO reasignar(Long ordenId, Long nuevoArtesanoId,
                                                 String motivo, Long realizadoPor) {
        try {
            ReasignacionRequestDTO dto = new ReasignacionRequestDTO();
            dto.setNuevoArtesanoId(nuevoArtesanoId);
            dto.setMotivo(motivo);
            return webClient.post()
                    .uri(u -> u.path("/ordenes/{id}/reasignar")
                            .queryParam("realizadoPor", realizadoPor)
                            .build(ordenId))
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(OrdenProduccionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al reasignar el artesano"));
        }
    }

    public List<HistorialOrdenResponseDTO> listarHistorial(Long ordenId) {
        try {
            return webClient.get()
                    .uri("/ordenes/{id}/historial", ordenId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<HistorialOrdenResponseDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al obtener el historial de la orden"));
        }
    }

    private String extraerMensaje(WebClientResponseException e, String porDefecto) {
        ErrorResponseDTO error = e.getResponseBodyAs(ErrorResponseDTO.class);
        return error != null ? error.getMensaje() : porDefecto;
    }
}
