package com.classic.craftorderview.controller.publico;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.services.CotizacionApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seguimiento")
public class SeguimientoWebController {

    private final CotizacionApiService cotizacionService;

    public SeguimientoWebController(CotizacionApiService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @GetMapping("/{token}")
    public String seguimiento(@PathVariable String token, Model model) {
        try {
            CotizacionResponseDTO cotizacion = cotizacionService.buscarPorToken(token);
            model.addAttribute(ModelAtributos.COTIZACION, cotizacion);
            model.addAttribute(ModelAtributos.TITULO_PAGINA,
                    "Estado de tu cotización");
            return Vistas.SEGUIMIENTO;
        } catch (Exception e) {
            return "redirect:/catalogo";
        }
    }
}
