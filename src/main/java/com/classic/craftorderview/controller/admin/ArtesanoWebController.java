package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.UsuarioRequestDTO;
import com.classic.craftorderview.model.dto.response.ContrasenaTemporalResponseDTO;
import com.classic.craftorderview.model.dto.response.PaginaResponseDTO;
import com.classic.craftorderview.model.dto.response.UsuarioResponseDTO;
import com.classic.craftorderview.services.UsuarioApiService;
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
@RequestMapping("/admin/artesanos")
public class ArtesanoWebController {

    private final UsuarioApiService usuarioService;

    public ArtesanoWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(defaultValue = "") String campoBusqueda,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        PaginaResponseDTO<UsuarioResponseDTO> pagina =
                usuarioService.listarArtesanosPaginado(busqueda, campoBusqueda, activo, page);

        model.addAttribute(ModelAtributos.TITULO_PAGINA, "Artesanos");
        model.addAttribute("artesanos", pagina.getContenido());
        model.addAttribute(ModelAtributos.PAGINA, pagina);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("campoBusqueda", campoBusqueda);
        model.addAttribute("activo", activo);
        UsuarioRequestDTO nuevo = new UsuarioRequestDTO();
        nuevo.setRol("ARTESANO");
        model.addAttribute("artesano", nuevo);

        Object contrasenaTemporal = session.getAttribute("contrasenaTemporal");
        if (contrasenaTemporal != null) {
            model.addAttribute("contrasenaTemporal", contrasenaTemporal);
            model.addAttribute("artesanoReseteado", session.getAttribute("artesanoReseteado"));
            session.removeAttribute("contrasenaTemporal");
            session.removeAttribute("artesanoReseteado");
        }

        Object errorArtesano = session.getAttribute("errorArtesano");
        if (errorArtesano != null) {
            model.addAttribute(ModelAtributos.ERROR, errorArtesano);
            session.removeAttribute("errorArtesano");
        }

        return Vistas.ADMIN_ARTESANOS;
    }

    @PostMapping
    public String crear(@ModelAttribute UsuarioRequestDTO artesano, Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            usuarioService.crear(artesano);
            return "redirect:/admin/artesanos";
        } catch (Exception e) {
            PaginaResponseDTO<UsuarioResponseDTO> pagina =
                    usuarioService.listarArtesanosPaginado("", "", null, 0);
            model.addAttribute(ModelAtributos.TITULO_PAGINA, "Artesanos");
            model.addAttribute("artesanos", pagina.getContenido());
            model.addAttribute(ModelAtributos.PAGINA, pagina);
            model.addAttribute("busqueda", "");
            model.addAttribute("campoBusqueda", "");
            model.addAttribute("activo", (Boolean) null);
            model.addAttribute("artesano", artesano);
            model.addAttribute(ModelAtributos.ERROR_MODAL, e.getMessage());
            model.addAttribute(ModelAtributos.ABRIR_MODAL, true);
            return Vistas.ADMIN_ARTESANOS;
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            usuarioService.desactivar(id);
        } catch (Exception e) {
            session.setAttribute("errorArtesano", e.getMessage());
        }
        return "redirect:/admin/artesanos";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        usuarioService.activar(id);
        return "redirect:/admin/artesanos";
    }

    @PostMapping("/{id}/resetear-contrasena")
    public String resetearContrasena(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        ContrasenaTemporalResponseDTO resp = usuarioService.resetearContrasena(id);
        String nombreArtesano = usuarioService.buscarPorId(id).getNombre();

        session.setAttribute("contrasenaTemporal", resp.getContrasenaTemporal());
        session.setAttribute("artesanoReseteado", nombreArtesano);
        return "redirect:/admin/artesanos";
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
