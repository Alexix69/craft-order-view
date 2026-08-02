package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.CotizacionRequestDTO;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    private String extraerMensaje(WebClientResponseException e, String porDefecto) {
        ErrorResponseDTO error = e.getResponseBodyAs(ErrorResponseDTO.class);
        return error != null ? error.getMensaje() : porDefecto;
    }
}
