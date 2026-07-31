package com.classic.craftorderview.controller.admin;

import com.classic.craftorderview.model.dto.request.UsuarioRequestDTO;
import com.classic.craftorderview.model.dto.response.ContrasenaTemporalResponseDTO;
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

import java.util.List;

@Controller
@RequestMapping("/admin/artesanos")
public class ArtesanoWebController {

    private final UsuarioApiService usuarioService;

    public ArtesanoWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        model.addAttribute("tituloPagina", "Artesanos");
        List<UsuarioResponseDTO> artesanos = usuarioService.listarPorRol("ARTESANO");
        model.addAttribute("artesanos", artesanos);
        UsuarioRequestDTO nuevo = new UsuarioRequestDTO();
        nuevo.setRol("ARTESANO");
        model.addAttribute("artesano", nuevo);

        Object errorArtesano = session.getAttribute("errorArtesano");
        if (errorArtesano != null) {
            model.addAttribute("errorArtesano", errorArtesano);
            session.removeAttribute("errorArtesano");
        }

        Object contrasenaTemporal = session.getAttribute("contrasenaTemporal");
        if (contrasenaTemporal != null) {
            model.addAttribute("contrasenaTemporal", contrasenaTemporal);
            model.addAttribute("artesanoReseteado", session.getAttribute("artesanoReseteado"));
            session.removeAttribute("contrasenaTemporal");
            session.removeAttribute("artesanoReseteado");
        }

        return "admin/artesanos/listar";
    }

    @PostMapping
    public String crear(@ModelAttribute UsuarioRequestDTO artesano, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        try {
            usuarioService.crear(artesano);
        } catch (Exception e) {
            session.setAttribute("errorArtesano", e.getMessage());
        }
        return "redirect:/admin/artesanos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, HttpSession session) {
        String redireccion = verificarSesion(session, "ADMIN");
        if (redireccion != null) {
            return redireccion;
        }

        usuarioService.desactivar(id);
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
        String nombreArtesano = usuarioService.listarPorRol("ARTESANO").stream()
                .filter(a -> a.getId().equals(id))
                .map(UsuarioResponseDTO::getNombre)
                .findFirst()
                .orElse("");

        session.setAttribute("contrasenaTemporal", resp.getContrasenaTemporal());
        session.setAttribute("artesanoReseteado", nombreArtesano);
        return "redirect:/admin/artesanos";
    }

    private String verificarSesion(HttpSession session, String rolRequerido) {
        String rol = (String) session.getAttribute("usuarioRol");
        if (rol == null) {
            return "redirect:/login";
        }
        if (!rolRequerido.equals(rol)) {
            return "ARTESANO".equals(rol)
                    ? "redirect:/artesano/tablero"
                    : "redirect:/admin/dashboard";
        }
        return null;
    }
}
