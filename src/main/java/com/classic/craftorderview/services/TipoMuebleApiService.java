package com.classic.craftorderview.services;

import com.classic.craftorderview.model.dto.request.TipoMuebleRequestDTO;
import com.classic.craftorderview.model.dto.response.ErrorResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.TipoMuebleResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;

@Service
public class TipoMuebleApiService {

    private final WebClient webClient;

    public TipoMuebleApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public PaginaResponseDTO<TipoMuebleResponseDTO> listarTodosPaginado(String nombre, int page) {
        try {
            return webClient.get()
                    .uri(u -> u.path("/tipos-mueble")
                            .queryParam("nombre", nombre != null ? nombre : "")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<TipoMuebleResponseDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public PaginaResponseDTO<TipoMuebleResponseDTO> listarActivosPaginado(String nombre, int page) {
        try {
            return webClient.get()
                    .uri(u -> u.path("/tipos-mueble/activos")
                            .queryParam("nombre", nombre != null ? nombre : "")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<PaginaResponseDTO<TipoMuebleResponseDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public List<String> listarUrlsActivas() {
        PaginaResponseDTO<TipoMuebleResponseDTO> pagina = listarActivosPaginado("", 0);
        return pagina.getContenido().stream()
                .map(TipoMuebleResponseDTO::getFotoUrl)
                .filter(url -> url != null && !url.isEmpty())
                .distinct()
                .toList();
    }

    public TipoMuebleResponseDTO buscarPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/tipos-mueble/{id}", id)
                    .retrieve()
                    .bodyToMono(TipoMuebleResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void crear(TipoMuebleRequestDTO dto, MultipartFile imagenFile) {
        MultiValueMap<String, Object> body = construirCuerpoMultipart(dto, imagenFile);
        try {
            webClient.post()
                    .uri("/tipos-mueble")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    public void actualizar(Long id, TipoMuebleRequestDTO dto, MultipartFile imagenFile) {
        MultiValueMap<String, Object> body = construirCuerpoMultipart(dto, imagenFile);
        try {
            webClient.put()
                    .uri("/tipos-mueble/{id}", id)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(extraerMensaje(e));
        }
    }

    private MultiValueMap<String, Object> construirCuerpoMultipart(TipoMuebleRequestDTO dto, MultipartFile imagenFile) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("nombre", dto.getNombre());
        body.add("descripcion", dto.getDescripcion() != null ? dto.getDescripcion() : "");
        body.add("costoBaseMo", dto.getCostoBaseMo().toString());
        if (dto.getFotoUrl() != null && !dto.getFotoUrl().isEmpty()) {
            body.add("fotoUrl", dto.getFotoUrl());
        }
        if (imagenFile != null && !imagenFile.isEmpty()) {
            body.add("imagenFile", new ByteArrayResource(getBytes(imagenFile)) {
                @Override
                public String getFilename() {
                    return imagenFile.getOriginalFilename();
                }
            });
        }
        return body;
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo");
        }
    }

    public void activar(Long id) {
        try {
            webClient.post()
                    .uri("/tipos-mueble/{id}/activar", id)
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
                    .uri("/tipos-mueble/{id}/desactivar", id)
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
