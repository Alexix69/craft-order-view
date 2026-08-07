package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.AprobacionRequestDTO;
import com.classic.craftorderview.model.dto.request.RechazoRequestDTO;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.services.CotizacionApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/cotizaciones")
public class CotizacionWebController {

    private final CotizacionApiService cotizacionService;

    public CotizacionWebController(CotizacionApiService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "PENDIENTE") String estado,
            @RequestParam(defaultValue = "0") int page,
            Model model, HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null)
            return check;

        PaginaResponseDTO<CotizacionResponseDTO> pagina = cotizacionService.listarPorEstado(estado, page);

        model.addAttribute("cotizaciones", pagina.getContenido());
        model.addAttribute(ModelAtributos.PAGINA, pagina);
        model.addAttribute(ModelAtributos.ESTADO, estado);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Cotizaciones");
        return Vistas.ADMIN_COTIZACIONES;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
            Model model, HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null)
            return check;

        CotizacionResponseDTO cotizacion = cotizacionService.buscarPorId(id);
        model.addAttribute(ModelAtributos.COTIZACION, cotizacion);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Detalle de cotización");
        model.addAttribute("costoSugerido", cotizacion.getCostoEstimado());

        String error = (String) session.getAttribute("errorCotizacion");
        if (error != null) {
            model.addAttribute(ModelAtributos.ERROR_MODAL, error);
            session.removeAttribute("errorCotizacion");
        }

        return Vistas.ADMIN_COTIZACION_DETALLE;
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id,
            @ModelAttribute AprobacionRequestDTO dto,
            HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null)
            return check;
        try {
            cotizacionService.aprobar(id, dto);
            return "redirect:/admin/cotizaciones?estado=APROBADA";
        } catch (Exception e) {
            session.setAttribute("errorCotizacion", e.getMessage());
            return "redirect:/admin/cotizaciones/" + id;
        }
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
            @ModelAttribute RechazoRequestDTO dto,
            HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null)
            return check;
        try {
            cotizacionService.rechazar(id, dto);
            return "redirect:/admin/cotizaciones?estado=RECHAZADA";
        } catch (Exception e) {
            session.setAttribute("errorCotizacion", e.getMessage());
            return "redirect:/admin/cotizaciones/" + id;
        }
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/tablero"
                    : "redirect:/admin/kanban";
        }
        return null;
    }
}
