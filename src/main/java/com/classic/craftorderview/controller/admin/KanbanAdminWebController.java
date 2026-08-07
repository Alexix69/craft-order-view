package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.CambioEstadoRequestDTO;
import com.classic.craftorderview.model.dto.request.ReasignacionRequestDTO;
import com.classic.craftorderview.model.dto.response.CotizacionResponseDTO;
import com.classic.craftorderview.model.dto.response.HistorialOrdenResponseDTO;
import com.classic.craftorderview.model.dto.response.OrdenProduccionResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import com.classic.craftorderview.services.CotizacionApiService;
import com.classic.craftorderview.services.OrdenApiService;
import com.classic.craftorderview.services.UsuarioApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/kanban")
public class KanbanAdminWebController {

    private final OrdenApiService ordenService;
    private final UsuarioApiService usuarioService;
    private final CotizacionApiService cotizacionService;

    public KanbanAdminWebController(OrdenApiService ordenService,
                                     UsuarioApiService usuarioService,
                                     CotizacionApiService cotizacionService) {
        this.ordenService = ordenService;
        this.usuarioService = usuarioService;
        this.cotizacionService = cotizacionService;
    }

    @GetMapping
    public String kanban(Model model, HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null) return check;

        List<OrdenProduccionResponseDTO> ordenes = ordenService.listarActivas();

        PaginaResponseDTO<CotizacionResponseDTO> pagadas =
                cotizacionService.listarPorEstado("PAGADA", 0);
        List<CotizacionResponseDTO> sinAsignar = pagadas.getContenido().stream()
                .filter(c -> c.getEstadoOrden() == null)
                .toList();

        List<UsuarioResponseDTO> artesanos =
                usuarioService.listarArtesanosPaginado("", "nombre", true, 0)
                        .getContenido();

        String errorKanban = (String) session.getAttribute("errorKanban");
        if (errorKanban != null) {
            model.addAttribute(ModelAtributos.ERROR, errorKanban);
            session.removeAttribute("errorKanban");
        }

        model.addAttribute(ModelAtributos.ORDENES, ordenes);
        model.addAttribute("sinAsignar", sinAsignar);
        model.addAttribute(ModelAtributos.ARTESANOS, artesanos);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Tablero de producción");
        return Vistas.ADMIN_KANBAN;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null) return check;

        OrdenProduccionResponseDTO orden = ordenService.buscarPorId(id);
        List<HistorialOrdenResponseDTO> historial = ordenService.listarHistorial(id);
        List<UsuarioResponseDTO> artesanos =
                usuarioService.listarArtesanosPaginado("", "nombre", true, 0)
                        .getContenido();

        model.addAttribute(ModelAtributos.ORDEN, orden);
        model.addAttribute(ModelAtributos.HISTORIAL, historial);
        model.addAttribute(ModelAtributos.ARTESANOS, artesanos);
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Detalle de orden");
        return Vistas.ADMIN_KANBAN_DETALLE;
    }

    @PostMapping("/asignar")
    public String asignar(@RequestParam Long cotizacionId,
                           @RequestParam Long artesanoId,
                           HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null) return check;
        Long adminId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
        try {
            ordenService.asignar(cotizacionId, artesanoId, adminId);
        } catch (Exception e) {
            session.setAttribute("errorKanban", e.getMessage());
        }
        return "redirect:/admin/kanban";
    }

    @PostMapping("/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                            @RequestBody CambioEstadoRequestDTO dto,
                                            HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL))) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No autorizado"));
        }
        Long adminId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
        try {
            OrdenProduccionResponseDTO orden = ordenService.cambiarEstado(
                    id, dto.getEstadoNuevo(), dto.getMotivo(), adminId);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reasignar")
    public String reasignar(@PathVariable Long id,
                             @ModelAttribute ReasignacionRequestDTO dto,
                             HttpSession session) {
        String check = verificarSesion(session, "ADMIN");
        if (check != null) return check;
        Long adminId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
        try {
            ordenService.reasignar(id, dto.getNuevoArtesanoId(), dto.getMotivo(), adminId);
        } catch (Exception e) {
            session.setAttribute("errorKanban", e.getMessage());
        }
        return "redirect:/admin/kanban/" + id;
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/kanban"
                    : "redirect:/admin/kanban";
        }
        return null;
    }
}
