package com.classic.craftorderview.controller.publico;

import com.classic.craftorderview.constantes.MensajesEstado;
import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.services.CotizacionApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pagar")
public class PagarWebController {

    private final CotizacionApiService cotizacionService;

    public PagarWebController(CotizacionApiService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @GetMapping("/{token}")
    public String mostrarPago(@PathVariable String token, Model model) {
        try {
            CotizacionResponseDTO cotizacion = cotizacionService.buscarPorToken(token);

            model.addAttribute(ModelAtributos.COTIZACION, cotizacion);
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Proceder al pago");

            String mensaje = switch (cotizacion.getEstado()) {
                case "PENDIENTE" -> MensajesEstado.COTIZACION_PENDIENTE;
                case "PAGADA" -> MensajesEstado.COTIZACION_PAGADA;
                case "RECHAZADA" -> MensajesEstado.COTIZACION_RECHAZADA_PREFIJO
                        + cotizacion.getMotivoRechazo();
                default -> null;
            };

            if (mensaje != null) {
                model.addAttribute("mensajeEstado", mensaje);
            }

            if (mensaje == null && !"APROBADA".equals(cotizacion.getEstado())) {
                return "redirect:/catalogo";
            }

            return Vistas.PAGAR;

        } catch (Exception e) {
            return "redirect:/catalogo";
        }
    }

    @PostMapping("/{token}")
    public String confirmarPago(@PathVariable String token) {
        try {
            cotizacionService.pagar(token);
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje == null || !mensaje.startsWith("ESTADO:")) {
                return "redirect:/catalogo";
            }
        }
        return "redirect:/pagar/" + token;
    }
}
