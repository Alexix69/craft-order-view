package com.classic.craftorderview.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PaginaResponseDTO<T> {

    private List<T> contenido;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
    private boolean esPrimera;
    private boolean esUltima;
}
