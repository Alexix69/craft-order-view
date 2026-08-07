package com.classic.craftorderview.controller;

import com.classic.craftorderview.constantes.ModelAtributos;
import com.classic.craftorderview.constantes.Vistas;
import com.classic.craftorderview.model.dto.request.CambiarContrasenaRequestDTO;
import com.classic.craftorderview.services.UsuarioApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CambiarContrasenaWebController {

    private final UsuarioApiService usuarioService;

    public CambiarContrasenaWebController(UsuarioApiService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cambiar-contrasena")
    public String mostrar(HttpSession session) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }

        if (!Boolean.TRUE.equals(session.getAttribute(ModelAtributos.SESSION_PRIMER_LOGIN))) {
            return "ARTESANO".equals(rol) ? "redirect:/artesano/tablero" : "redirect:/admin/kanban";
        }

        return Vistas.CAMBIAR_CONTRASENA;
    }

    @PostMapping("/cambiar-contrasena")
    public String procesar(@ModelAttribute CambiarContrasenaRequestDTO dto,
                            HttpSession session,
                            Model model) {
        String rol = (String) session.getAttribute(ModelAtributos.SESSION_USUARIO_ROL);
        if (rol == null) {
            return "redirect:/login";
        }

        try {
            Long usuarioId = (Long) session.getAttribute(ModelAtributos.SESSION_USUARIO_ID);
            usuarioService.cambiarContrasena(usuarioId, dto);
            session.removeAttribute(ModelAtributos.SESSION_PRIMER_LOGIN);
            return "ARTESANO".equals(rol) ? "redirect:/artesano/tablero" : "redirect:/admin/kanban";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return Vistas.CAMBIAR_CONTRASENA;
        }
    }
}
