package com.classic.craftorderview.controller.artesano;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/artesano/tablero")
public class ArtesanoTableroWebController {

    @GetMapping
    public String mostrar(Model model, HttpSession session) {
        String redireccion = verificarSesion(session, "ARTESANO");
        if (redireccion != null) {
            return redireccion;
        }

        model.addAttribute("tituloPagina", "Mi Tablero");
        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        return "artesano/tablero/tablero";
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
