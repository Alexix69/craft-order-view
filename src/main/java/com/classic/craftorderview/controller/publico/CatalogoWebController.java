package com.classic.craftorderview.controller.publico;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.TipoMuebleResponseDTO;
import com.classic.craftorderview.services.TipoMuebleApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalogo")
public class CatalogoWebController {

    private final TipoMuebleApiService tipoMuebleService;

    public CatalogoWebController(TipoMuebleApiService tipoMuebleService) {
        this.tipoMuebleService = tipoMuebleService;
    }

    @GetMapping
    public String catalogo(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        PaginaResponseDTO<TipoMuebleResponseDTO> pagina =
                tipoMuebleService.listarActivosPaginado(nombre, page);

        model.addAttribute("tiposMueble", pagina.getContenido());
        model.addAttribute(ModelAtributos.PAGINA, pagina);
        model.addAttribute(ModelAtributos.NOMBRE_FILTRO, nombre);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Catálogo de Muebles");
        return Vistas.CATALOGO;
    }
}
