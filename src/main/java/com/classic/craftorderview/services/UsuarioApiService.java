package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.CambiarContrasenaRequestDTO;
import com.classic.craftorderview.model.dto.request.LoginRequestDTO;
import com.classic.craftorderview.model.dto.request.UsuarioRequestDTO;
import com.classic.craftorderview.model.dto.response.ContrasenaTemporalResponseDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class UsuarioApiService {

    private final WebClient webClient;

    public UsuarioApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public UsuarioResponseDTO autenticar(LoginRequestDTO dto) {
        try {
            return webClient.post()
                    .uri("/auth/login")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public PaginaResponseDTO<UsuarioResponseDTO> listarArtesanosPaginado(
            String busqueda, String campoBusqueda, Boolean activo, int page) {
        try {
            return webClient.get()
                    .uri(u -> {
                        var builder = u.path("/usuarios")
                                .queryParam("rol", "ARTESANO")
                                .queryParam("busqueda", busqueda != null ? busqueda : "")
                                .queryParam("campoBusqueda", campoBusqueda != null ? campoBusqueda : "")
                                .queryParam("page", page);
                        if (activo != null) {
                            builder = builder.queryParam("activo", activo);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<UsuarioResponseDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void crear(UsuarioRequestDTO dto) {
        try {
            webClient.post()
                    .uri("/usuarios")
                    .bodyValue(dto)
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
                    .uri("/usuarios/{id}/desactivar", id)
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
                    .uri("/usuarios/{id}/activar", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public ContrasenaTemporalResponseDTO resetearContrasena(Long id) {
        try {
            return webClient.post()
                    .uri("/usuarios/{id}/resetear-contrasena", id)
                    .retrieve()
                    .bodyToMono(ContrasenaTemporalResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void cambiarContrasena(Long id, CambiarContrasenaRequestDTO dto) {
        try {
            webClient.post()
                    .uri("/usuarios/{id}/cambiar-contrasena", id)
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
