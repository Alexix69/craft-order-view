package com.classic.craftorderview.controller.publico;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.CotizacionRequestDTO;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.model.dto.response.TipoMuebleResponseDTO;
import com.classic.craftorderview.services.CotizacionApiService;
import com.classic.craftorderview.services.MaterialApiService;
import com.classic.craftorderview.services.TipoMuebleApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/cotizar")
public class CotizarWebController {

    private static final String SESSION_COTIZACION_CALCULADA = "cotizacionCalculada";
    private static final String SESSION_COTIZACION_REQUEST = "cotizacionRequest";
    private static final String SESSION_COTIZACION_TOKEN = "cotizacionToken";
    private static final String PREFIJO_INACTIVO = "INACTIVO:";

    private final CotizacionApiService cotizacionService;
    private final TipoMuebleApiService tipoMuebleService;
    private final MaterialApiService materialService;

    @Value("${app.url.base}")
    private String appBaseUrl;

    public CotizarWebController(CotizacionApiService cotizacionService,
                                 TipoMuebleApiService tipoMuebleService,
                                 MaterialApiService materialService) {
        this.cotizacionService = cotizacionService;
        this.tipoMuebleService = tipoMuebleService;
        this.materialService = materialService;
    }

    @GetMapping
    public String mostrarFormulario(
            @RequestParam(required = false) Long tipoMuebleId,
            Model model,
            HttpSession session) {

        model.addAttribute("tiposMueble", tipoMuebleService.listarActivos());
        model.addAttribute("materiales", materialService.listarActivos());
        model.addAttribute("acabados", materialService.listarAcabados());
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Configurar mi mueble");

        CotizacionRequestDTO previo =
                (CotizacionRequestDTO) session.getAttribute(SESSION_COTIZACION_REQUEST);

        if (previo != null) {
            model.addAttribute("datosPrevios", previo);
        } else if (tipoMuebleId != null) {
            model.addAttribute("tipoMuebleIdSeleccionado", tipoMuebleId);
        }

        return Vistas.COTIZAR;
    }

    @PostMapping
    public String calcular(
            @ModelAttribute CotizacionRequestDTO dto,
            HttpSession session,
            Model model) {
        try {
            CotizacionResponseDTO calculada = cotizacionService.calcular(dto);
            session.setAttribute(SESSION_COTIZACION_CALCULADA, calculada);
            session.setAttribute(SESSION_COTIZACION_REQUEST, dto);
            return "redirect:/cotizar/confirmar";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.startsWith(PREFIJO_INACTIVO)) {
                return "redirect:/catalogo?nombre=" + codificar(mensaje.substring(PREFIJO_INACTIVO.length()));
            }
            model.addAttribute("tiposMueble", tipoMuebleService.listarActivos());
            model.addAttribute("materiales", materialService.listarActivos());
            model.addAttribute("acabados", materialService.listarAcabados());
            model.addAttribute(ModelAtributos.ERROR_MODAL, mensaje);
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Configurar mi mueble");
            return Vistas.COTIZAR;
        }
    }

    @GetMapping("/confirmar")
    public String mostrarConfirmacion(HttpSession session, Model model) {
        CotizacionResponseDTO calculada =
                (CotizacionResponseDTO) session.getAttribute(SESSION_COTIZACION_CALCULADA);
        CotizacionRequestDTO request =
                (CotizacionRequestDTO) session.getAttribute(SESSION_COTIZACION_REQUEST);

        if (calculada == null || request == null) {
            return "redirect:/cotizar";
        }

        model.addAttribute("cotizacion", calculada);
        model.addAttribute("request", request);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Confirmar cotización");

        try {
            TipoMuebleResponseDTO tipoMueble =
                    tipoMuebleService.buscarPorId(request.getTipoMuebleId());
            model.addAttribute("fotoMueble", tipoMueble.getFotoUrl());
            model.addAttribute("nombreMueble", tipoMueble.getNombre());
        } catch (Exception e) {
            model.addAttribute("fotoMueble", null);
            model.addAttribute("nombreMueble", "Mueble seleccionado");
        }

        return Vistas.COTIZAR_CONFIRMAR;
    }

    @PostMapping("/confirmar")
    public String confirmar(HttpSession session) {
        CotizacionRequestDTO request =
                (CotizacionRequestDTO) session.getAttribute(SESSION_COTIZACION_REQUEST);

        if (request == null) {
            return "redirect:/cotizar";
        }

        try {
            CotizacionResponseDTO confirmada = cotizacionService.confirmar(request);
            session.removeAttribute(SESSION_COTIZACION_CALCULADA);
            session.removeAttribute(SESSION_COTIZACION_REQUEST);
            session.setAttribute(SESSION_COTIZACION_TOKEN, confirmada.getToken());
            return "redirect:/cotizar/exito";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.startsWith(PREFIJO_INACTIVO)) {
                session.removeAttribute(SESSION_COTIZACION_CALCULADA);
                session.removeAttribute(SESSION_COTIZACION_REQUEST);
                return "redirect:/catalogo?nombre=" + codificar(mensaje.substring(PREFIJO_INACTIVO.length()));
            }
            return "redirect:/cotizar/confirmar";
        }
    }

    @GetMapping("/exito")
    public String exito(HttpSession session, Model model) {
        String token = (String) session.getAttribute(SESSION_COTIZACION_TOKEN);
        if (token == null) {
            return "redirect:/catalogo";
        }
        session.removeAttribute(SESSION_COTIZACION_TOKEN);

        String linkSeguimiento = appBaseUrl + "/seguimiento/" + token;
        model.addAttribute("linkSeguimiento", linkSeguimiento);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "¡Cotización enviada!");
        return Vistas.COTIZAR_EXITO;
    }

    private String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }
}
