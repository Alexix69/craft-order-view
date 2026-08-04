package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.AprobacionRequestDTO;
import com.classic.craftorderview.model.dto.request.CotizacionRequestDTO;
import com.classic.craftorderview.model.dto.request.RechazoRequestDTO;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class CotizacionApiService {

    private final WebClient webClient;

    public CotizacionApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public CotizacionResponseDTO calcular(CotizacionRequestDTO dto) {
        try {
            return webClient.post()
                    .uri("/cotizaciones/calcular")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al calcular la cotización"));
        }
    }

    public CotizacionResponseDTO confirmar(CotizacionRequestDTO dto) {
        try {
            return webClient.post()
                    .uri("/cotizaciones")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al confirmar la cotización"));
        }
    }

    public CotizacionResponseDTO buscarPorToken(String token) {
        try {
            return webClient.get()
                    .uri("/cotizaciones/seguimiento/{token}", token)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Cotización no encontrada"));
        }
    }

    public PaginaResponseDTO<CotizacionResponseDTO> listarPorEstado(String estado, int page) {
        try {
            return webClient.get()
                    .uri(u -> u.path("/cotizaciones")
                            .queryParam("estado", estado)
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<CotizacionResponseDTO>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al listar las cotizaciones"));
        }
    }

    public CotizacionResponseDTO buscarPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/cotizaciones/{id}", id)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Cotización no encontrada"));
        }
    }

    public CotizacionResponseDTO aprobar(Long id, AprobacionRequestDTO dto) {
        try {
            return webClient.post()
                    .uri("/cotizaciones/{id}/aprobar", id)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al aprobar la cotización"));
        }
    }

    public CotizacionResponseDTO rechazar(Long id, RechazoRequestDTO dto) {
        try {
            return webClient.post()
                    .uri("/cotizaciones/{id}/rechazar", id)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al rechazar la cotización"));
        }
    }

    public Map<String, Object> buscarTokenAdmin(String token) {
        try {
            return webClient.get()
                    .uri("/cotizaciones/buscar/{token}", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Token no encontrado");
        }
    }

    public CotizacionResponseDTO pagar(String token) {
        try {
            return webClient.post()
                    .uri("/cotizaciones/pagar/{token}", token)
                    .retrieve()
                    .bodyToMono(CotizacionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e, "Error al procesar el pago"));
        }
    }

    private String extraerMensaje(WebClientResponseException e, String porDefecto) {
        ErrorResponseDTO error = e.getResponseBodyAs(ErrorResponseDTO.class);
        return error != null ? error.getMensaje() : porDefecto;
    }
}
