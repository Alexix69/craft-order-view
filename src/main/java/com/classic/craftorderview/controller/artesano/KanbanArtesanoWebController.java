package com.classic.craftorderview.controller.artesano;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.CambioEstadoRequestDTO;
import com.classic.craftorderview.model.dto.response.OrdenProduccionResponseDTO;
import com.classic.craftorderview.services.OrdenApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/artesano/kanban")
public class KanbanArtesanoWebController {

    private final OrdenApiService ordenService;

    public KanbanArtesanoWebController(OrdenApiService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public String kanban(Model model, HttpSession session) {
        String check = verificarSesion(session, "ARTESANO");
        if (check != null) return check;
        Long artesanoId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
        model.addAttribute(ModelAtributos.ORDENES, ordenService.listarPorArtesano(artesanoId));
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Mi tablero");
        return Vistas.ARTESANO_KANBAN;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, HttpSession session) {
        String check = verificarSesion(session, "ARTESANO");
        if (check != null) return check;
        model.addAttribute(ModelAtributos.ORDEN, ordenService.buscarPorId(id));
        model.addAttribute(ModelAtributos.HISTORIAL, ordenService.listarHistorial(id));
        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Detalle de mi orden");
        return Vistas.ARTESANO_KANBAN_DETALLE;
    }

    @PostMapping("/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                            @RequestBody CambioEstadoRequestDTO dto,
                                            HttpSession session) {
        if (!"ARTESANO".equals(session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL))) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No autorizado"));
        }
        Long artesanoId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
        try {
            OrdenProduccionResponseDTO orden =
                    ordenService.cambiarEstado(id, dto.getEstadoNuevo(), null, artesanoId);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
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
